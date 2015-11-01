package com.example.tagrank.backend.spark

import java.lang.Character.UnicodeBlock

import akka.actor._
import com.example.tagrank.api.Ranking
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Milliseconds, Seconds, Minutes, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.Status

object SparkLogic {

  /**
   * 解析に使う analyzeRanking, analyzeRankingWithStream のいずれかのメソッド名を指定してください
   */
  val analyzeLogic: analyzeLogicType = analyzeRanking

  /**
   * クラスパス上にある tweets.txt のパス
   *
   * ファイルの中身は下記のファイルを参照してください。
   * (project root)/modules/backend/src/main/resources/tweets.txt
   */
  lazy val tweetsFilePath =
    getClass.getClassLoader.getResource("tweets.txt").toString

  /**
   * ① テキストファイルからツイートを読み込んで解析
   *
   * @param sc SparkContextです
   * @param ssc (ここでは無視してください)
   * @param receiver 解析した結果をこのオブジェクトに渡します
   */
  def analyzeRanking(sc: SparkContext, ssc: StreamingContext, receiver: ActorSelection): Unit = {
    
    // tweets.txt の中にある全ツイートの RDD
    val tweetsRDD: RDD[String] = sc.textFile(tweetsFilePath)

    // Ranking に変換する RDD
    val rankingsRDD: RDD[Ranking] =
      tweetsRDD map { tweet: String =>
        // String を Ranking ケースクラスに変換
        Ranking("#hashTag", rank = 1, Array(tweet), sampleCount = 1)
      }

    // collect() を呼び出すことによって実際の処理が始まる
    val rankings = rankingsRDD.collect()

    // フロントエンドに結果を渡す
    receiver ! rankings
  }

  /**
   * ② Spark Streams を使ってリアルタイムにツイートを解析
   *
   * @param sc SparkContext です
   * @param ssc SparkContext から作った StreamingContext です
   * @param receiver 解析した結果をこのオブジェクトに渡します
   */
  def analyzeRankingWithStream(sc: SparkContext, ssc: StreamingContext, receiver: ActorSelection): Unit = {

    // Twitter の DStream
    val twitterStream: ReceiverInputDStream[Status] =
      TwitterUtils.createStream(ssc, None)

    // ツイート の DStream に変換
    val tweetStream: DStream[String] =
      twitterStream map { status =>
        status.getText
      }

    // ストリームの塊を処理する
    tweetStream.foreachRDD { rdd: RDD[String] =>

        // Ranking に変換する RDD
        val rankingsRDD = rdd map { tweet: String =>
            // String を Ranking ケースクラスに変換
            Ranking("#hashTag", rank = 1, Array(tweet), sampleCount = 1)
        }

        // collect() を呼び出すことによって実際の RDD の処理が始まる
        val rankings = rankingsRDD.collect()

        receiver ! rankings
      }

    // start() を呼び出すことによって上記で定義した Stream の処理が始まる
    ssc.start()
  }


  /**
   * 文字列に日本語特有の文字(ひらがな/カタカナ)が含まれているかどうかを返します
   *
   * @param s 文字列
   * @return true: 日本語の文字が含まれている | false: 日本語の文字が含まれていない
   */
  def containsJapaneseChar(s: String): Boolean = {
    val blocks = s.map(UnicodeBlock.of(_))
    blocks.contains(UnicodeBlock.HIRAGANA) || blocks.contains(UnicodeBlock.KATAKANA)
  }

  /**
   * 文字列からTwitterのハッシュタグのみを抽出します
   *
   * @param s 文字列
   * @return 文字列に含まれるTwitterハッシュタグ ( #xxxx 形式の文字列 ) の集合
   */
  def pickHashTags(s: String): Set[String] = {
    """#[[^\s]&&[^\p{Punct}]]+""".r.findAllMatchIn(s).map(_.matched).toSet
  }

  /**
   * 解析する関数の引数と返り値を定義しています (読みやすさのため)
   */
  type analyzeLogicType = (SparkContext, StreamingContext, ActorSelection) => Unit

}
