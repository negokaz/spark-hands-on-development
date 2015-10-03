package com.example.tagrank.backend

import java.io.{File, PrintWriter}

import akka.actor.ActorSystem
import com.example.tagrank.backend.actor.RankingAnalyzer
import com.example.tagrank.backend.spark.SparkLogic
import com.typesafe.config.ConfigFactory
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * TwitterからTweetを取得してファイルに保存するメインクラス
 */
object TweetCollector extends Config {

  val config = ConfigFactory.load()

  System.setProperty("twitter4j.oauth.consumerKey", twitterConsumerKey)
  System.setProperty("twitter4j.oauth.consumerSecret", twitterConsumerSecret)
  System.setProperty("twitter4j.oauth.accessToken", twitterAccessToken)
  System.setProperty("twitter4j.oauth.accessTokenSecret", twitterAccessTokenSecret)

  val conf = new SparkConf()
    .setAppName("tweet-collector")
    .setMaster("local[2]")

  val sc = new SparkContext(conf)

  val ssc = new StreamingContext(sc, Seconds(1))

  val twitterStream = TwitterUtils.createStream(ssc, None)

  val file = new File(getClass.getClassLoader.getResource("tweets.txt").toURI)

  val writer = new PrintWriter(file, "UTF8")

  def lineBreakToSpace(s: String): String = {
    s.replaceAll("\\n", " ")
  }

  def main(args: Array[String]): Unit = {

    var count = 0

    twitterStream.map(_.getText)
      .filter(SparkLogic.pickHashTags(_).size > 0)
      .map(lineBreakToSpace)
      .foreachRDD { rdd =>
        rdd.collect().foreach { tweet =>
          count += 1
          println(s"$count: $tweet")
          writer.println(tweet)
          writer.flush()
        }
      }

    println(s"write to ${file.getPath}")
    ssc.start()
    ssc.awaitTermination()
    writer.close()
  }
}



