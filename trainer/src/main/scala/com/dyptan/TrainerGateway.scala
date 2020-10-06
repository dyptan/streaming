package com.dyptan

import java.io.InputStream
import java.util.Properties
import java.util.logging.Logger

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{path, _}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json._

import scala.concurrent.duration._

object TrainerActor {
  case class TrainRequest(trainingDataSetPath: String, limit: Int, iterations: Int)
  case class ApplyRequest(path: String)
}

class TrainerActor extends Actor with ActorLogging {
  import TrainerActor._
  val trainer = new Trainer

  override def receive: Receive = readyToTrain()

  def readyToTrain(): Receive = {
    case TrainRequest(path, limit, iterations) =>
      log.info(s"Train request received with path: $path, limit: $limit,iterations: $iterations")

      trainer.setSource(new java.net.URL(path), limit, iterations)
      trainer.train()
      log.info(s"Training completed")

      sender() ! trainer.evaluate()
      context.become(readyToApply(), false)
      }

  def readyToApply(): Receive = {
    case ApplyRequest(path) =>
      log.info("Applying model...")
      try { trainer.save(path)
      } catch {
        case e: Throwable => sender() ! e.toString()
      }
      log.info(s"New model saved to "+path)

      sender() ! "true"
      context.unbecome()
  }
}

class TrainerGateway {

//  import org.slf4j.Logger
  import org.slf4j.LoggerFactory
  val log = LoggerFactory.getLogger(classOf[TrainerGateway])

  val propertiesFile: InputStream = getClass.getClassLoader.getResourceAsStream("conf/application.properties")
  val properties = new Properties()
  // does not work with relative path
//  val source = Source.fromFile("conf/application.properties")
  properties.load(propertiesFile)



    implicit val system = ActorSystem("TrainerGateway")
    implicit val materializer = ActorMaterializer()
    import TrainerActor._
    import system.dispatcher
    implicit val defaultTimeout = Timeout(3000 seconds)

    val trainerActor = system.actorOf(Props[TrainerActor], "TRainerActor")

    log.info("Starting actorSystem "+this.getClass().getPackage().getImplementationVersion())

    val trainerServerRoute =
      path("api" / "train") {
        parameters(
          'path.as[String],
          'iterations.as[Int],
          'limit.as[Int])
          { (path: String, iterations: Int, limit: Int) =>
          post {
            val trainerResponseFuture = (trainerActor ? TrainRequest(path, limit, iterations))
              .mapTo[String]

            val entityFuture = trainerResponseFuture.map { responseOption =>
              HttpEntity(
                ContentTypes.`text/plain(UTF-8)`,
                responseOption
              )
            }
            complete(entityFuture)
          }
        }
      } ~
      path("api" / "apply") {
        parameters()
        {
          post {
            val trainerResponseFuture = (trainerActor ? ApplyRequest)
              .mapTo[String]
            val entityFuture = trainerResponseFuture.map { responseOption =>
              HttpEntity(
                ContentTypes.`text/plain(UTF-8)`,
                responseOption
              )
            }
            complete(entityFuture)
          }
        }
      }

    val port = properties.getOrDefault("gateway.port", "8081").asInstanceOf[String].toInt

    Http().bindAndHandle(trainerServerRoute, "0.0.0.0", port)
    log.info("Trainer Gateway started to listen on port " + port)

}

trait TrainRequestJsonProtocol extends DefaultJsonProtocol {
  import TrainerActor._
  implicit val requestFormat = jsonFormat3(TrainRequest)
}

object Main {
  def main(args: Array[String]): Unit = {
   val trainer = new TrainerGateway
  }
}
