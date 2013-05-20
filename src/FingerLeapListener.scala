import com.leapmotion.leap._
import javax.sound.midi._
import scala.collection.mutable._
import scala.collection.JavaConversions._


class FingerLeapListener (targetDevice: MidiDevice) extends Listener {
  
	// Contains values which are currently not at their default
	// these values will be reset when there are no visible hands
	private val activeMidiControlValues = new HashMap[Int, Int]
	private val targetReceiver = targetDevice.getReceiver()
	
	private var needsMidiReset = false
	
	override def onInit(controller: Controller) {
	    targetDevice.open()
        System.out.println("Generic MIDI Listener Initialized");
    }

    override def onConnect(controller: Controller) {
        System.out.println("Leap Device Connected");
    }

    override def onDisconnect(controller: Controller) {
        System.out.println("Leap Device Disconnected");
    }

    override def onExit(controller: Controller) {
        System.out.println("Exited");
    }

    // Process the new frame
    override def onFrame(controller: Controller) {
    	val frame = controller.frame();
    	
    	// If there are hands present, process them
    	// otherwise reset the midi values which have been sent
    	if (!frame.hands().empty()) {
    	  handleFrame(frame)
    	  needsMidiReset = true
    	} else if (needsMidiReset) {
    	  resetControlChangeValues()
    	  needsMidiReset = false
    	}
    }
    
    // Handle a frame (expects at least one hand)
    private def handleFrame(frame: Frame) {
      val hand = frame.hands().get(0)
      
      val fingers = hand.fingers
      
      if (!fingers.empty()) {
    	  // Calculate the hand's average finger tip position
          var avgPos = Vector.zero();
          fingers.foreach((finger) => {avgPos = avgPos.plus(finger.tipPosition())})
        	  
      
          avgPos = avgPos.divide(fingers.count());
          System.out.println("Hand has " + fingers.count()
                                 + " fingers, average finger tip position: " + avgPos);
      
	      //sendControlChange(1, palmRollMapped.toInt, 0)
	      //sendControlChange(0, palmPositionMapped.toInt, 0)
      

	      activeMidiControlValues.put(0, 0)
	      activeMidiControlValues.put(1, 0)
	      activeMidiControlValues.put(2, 0)
      }
     
    }
    
    
    private def resetControlChangeValues() {
      activeMidiControlValues.foreach{
        case (key, value) => sendControlChange(key, value, 0)
      }
    }
    
    private def sendControlChange(cc: Int, value: Int, channel: Int) {
      val midiMessage = new ShortMessage()
      midiMessage.setMessage(ShortMessage.CONTROL_CHANGE, channel, cc, value)
      
      targetReceiver.send(midiMessage, -1);
    }
    
    // Convert a number in one range to it's equiv in another range
    // This function is used for converting sensor values to midi values
    private def mapToRange(x: Double, in_min: Double, in_max: Double, out_min: Double, out_max: Double ) = { 
      if (x < in_max && x > in_min) {
        ((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min)
      } else {
        if (x < in_max) out_min
        else out_max
      }
    }
    
}
 