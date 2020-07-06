// import akka.actor.{ActorSystem, Props}
// import akka.pattern.ask
// import akka.stream.ActorMaterializer
// import akka.util.Timeout
// import com.dyptan.akka._
// import org.junit.Test
// import org.junit.jupiter.api.Assertions.assertEquals

// import scala.concurrent.duration._


// class AkkaTest {
//   implicit val system = ActorSystem("HighLevelExample")
//   implicit val materializer = ActorMaterializer()
//   import TrainerActor._
//   import system.dispatcher
//   implicit val defaultTimeout = Timeout(2 seconds)

//   val trainerActor = system.actorOf(Props[TrainerActor], "TRainerActor2")


//   def test {

//   var line = new String
//     def acum(s: String): Unit = {
//       line = s
//     }

//     (trainerActor ? TrainRequest("http://elastic:9200", 1, 1)).mapTo[String].foreach(acum)

//     Thread.sleep(100)
//     assertEquals("[{\"path\":\"http://elastic:9200\"}]", line)
//   }
// }