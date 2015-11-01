package com.example.tagrank.backend

import akka.actor.{Props, Actor, ActorSystem}
import com.example.tagrank.backend.Backend._
import com.example.tagrank.backend.actor.RankingAnalyzer
import com.example.tagrank.backend.spark.SparkLogic
import com.typesafe.config.ConfigFactory
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.spark.{SparkContext, SparkConf}

object Debugger extends App with Config {

  val config = ConfigFactory.load()

  System.setProperty("twitter4j.oauth.consumerKey", twitterConsumerKey)
  System.setProperty("twitter4j.oauth.consumerSecret", twitterConsumerSecret)
  System.setProperty("twitter4j.oauth.accessToken", twitterAccessToken)
  System.setProperty("twitter4j.oauth.accessTokenSecret", twitterAccessTokenSecret)

  def buildSparkConf = {

    val sparkConf = new SparkConf()
      .setAppName("twitter-hashtag-ranking")
      .setMaster("local[2]")

    sparkConf
  }

  //////// Actor System の起動 ////////

  val actorSystem = ActorSystem("backend")

  val sc = new SparkContext(buildSparkConf)

  val ssc = new StreamingContext(sc, Milliseconds(500))

  actorSystem.actorOf(Props[SimpleDebugActor], "debugger")

  ssc.awaitTermination()

  class SimpleDebugActor extends Actor {

    def receive = {

      case msg: Array[_] =>
        msg.foreach(println)

    }

    override def preStart() = {
      SparkLogic.analyzeLogic(sc, ssc, actorSystem.actorSelection("/user/debugger"))
    }
  }
}
