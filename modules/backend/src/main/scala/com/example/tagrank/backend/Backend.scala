package com.example.tagrank.backend

import akka.actor.ActorSystem
import com.example.tagrank.backend.actor.RankingAnalyzer
import com.typesafe.config.ConfigFactory
import org.apache.spark.{SparkContext, SparkConf}

/**
 * バックエンドを起動するためのメインクラス
 *
 * 第１引数: master (指定が無い場合はデフォルト)
 */
object Backend extends App with Config {

  val config = ConfigFactory.load()

  System.setProperty("twitter4j.oauth.consumerKey", twitterConsumerKey)
  System.setProperty("twitter4j.oauth.consumerSecret", twitterConsumerSecret)
  System.setProperty("twitter4j.oauth.accessToken", twitterAccessToken)
  System.setProperty("twitter4j.oauth.accessTokenSecret", twitterAccessTokenSecret)

  def buildSparkConf = {

    val sparkConf = new SparkConf().setAppName("twitter-hashtag-ranking")

    if (args.length > 0) {
      sparkConf.setMaster(args(0))
    } else {
      sparkConf
    }
  }

  //////// Actor System の起動 ////////

  val actorSystem = ActorSystem("backend")

  val sc = new SparkContext(buildSparkConf)

  actorSystem.actorOf(RankingAnalyzer.props(sc), "rankingAnalyzer")

  actorSystem.awaitTermination()
}



