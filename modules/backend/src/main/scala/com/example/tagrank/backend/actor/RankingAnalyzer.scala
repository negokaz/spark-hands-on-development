package com.example.tagrank.backend.actor

import akka.actor._
import com.example.tagrank.backend.spark.SparkLogic
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{StreamingContextState, Milliseconds, StreamingContext}

object RankingAnalyzer {

  def props(sc: SparkContext) = Props(new RankingAnalyzer(sc))

}

class RankingAnalyzer(sc: SparkContext) extends Actor with ActorLogging {

  import com.example.tagrank.api._

  val ssc = new StreamingContext(sc, Milliseconds(500))

  lazy val rankingReceiver =
     context.actorSelection("akka.tcp://application@127.0.0.1:2551/system/websockets/*/handler")

  def receive = {

    case AnalyzeRanking if (ssc.getState() == StreamingContextState.INITIALIZED) =>
      SparkLogic.analyzeLogic(sc, ssc, rankingReceiver)
  }

  override def preStart() = {
    log.info("=== RankingAnalyzer started ===")
  }

  override def postStop() = {
    ssc.stop(stopSparkContext = true, stopGracefully = false)
  }

}