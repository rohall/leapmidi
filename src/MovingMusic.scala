import com.leapmotion.leap._
import java.io.IOException
import scala.collection.JavaConversions._
import javax.sound.midi._

object MovingMusic {
   def main(args: Array[String]) {
      println("Moving Music - Starting Listeners")
      
      println("Select Target (Index starting at 0, but you know that)")
      
      MidiSystem.getMidiDeviceInfo().foreach{
        case (device) => println(device)
      }

      val targetDeviceInfo = MidiSystem.getMidiDeviceInfo()(Console.readInt())
      val targetDevice = MidiSystem.getMidiDevice(targetDeviceInfo)
        
      // Define a litener and a controller
      val leapListener = new FingerLeapListener(targetDevice)
      val leapController = new Controller()
      
      leapController.addListener(leapListener)
      
      // Keep this process running until Enter is pressed
      println("Ready!  Press Enter to quit...")
      
      try {
          System.in.read()
      } catch {
        case ioe: IOException => ioe.printStackTrace()
      }

      // Remove the sample listener when done
      leapController.removeListener(leapListener)
      
    }
}
