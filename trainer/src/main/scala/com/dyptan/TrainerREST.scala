package com.dyptan

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
}

class TrainerActor extends Actor with ActorLogging {
  import TrainerActor._
  val trainer = new ModelTrainer

  override def receive(): Receive = {
    case TrainRequest(path, limit, iterations) =>
      log.info(s"Train request received with path: $path, limit: $limit,iterations: $iterations")

      trainer.setSource(new java.net.URL(path), limit, iterations)
      trainer.train()
      trainer.save()

  }
}

trait TrainRequestJsonProtocol extends DefaultJsonProtocol {
  import TrainerActor._
  implicit val requestFormat = jsonFormat3(TrainRequest)
}

object AkkaHTTP extends App {

  implicit val system = ActorSystem("HighLevelExample")
  implicit val materializer = ActorMaterializer()
  import TrainerActor._
  import system.dispatcher
  implicit val defaultTimeout = Timeout(300 seconds)

  val trainerActor = system.actorOf(Props[TrainerActor], "TRainerActor")

  val trainerServerRoute =
    path("api" / "trainer") {
      parameters('path.as[String],
        'iterations.as[Int],
        'limit.as[Int]) { (path, limit, iterations) =>
        post {
          val trainerResponseFuture = (trainerActor ? TrainRequest(path , limit, iterations))
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

  Http().bindAndHandle(trainerServerRoute, "localhost", 8088)
}
