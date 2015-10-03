package com.example.tagrank


/**
 * バックエンド(ハッシュタグの順位を解析するサーバー)のAPI
 */
package object api {

  /**
   * ハッシュタグの順位の解析をリクエストするメッセージ
   */
  case class AnalyzeRanking()

  /**
   * ハッシュタグの順位
   *
   * @param hashTag ハッシュタグ
   * @param rank 全ツイートに内におけるハッシュタグの順位
   * @param sampleTweets ハッシュタグを含むツイートの一覧
   * @param sampleCount ハッシュタグを含むツイートの数
   */
  case class Ranking(hashTag: String, rank: Long, sampleTweets: Array[String], sampleCount: Long) {
    require(formattedAsHashTag(hashTag))
    require(rank >= 1)
    require(sampleTweets.size >= 1)

    override def toString: String = {
      val tweets = sampleTweets.map(_.substring(0, 10) + "~").mkString("[", ", ", "]")
      s"""Ranking($hashTag, rank: $rank, $tweets, sampleCount: $sampleCount)"""
    }
  }

  /**
   * @param maybeHashTag ハッシュタグかどうか検査する文字列
   * @return true: ハッシュタグの書式になっている場合 | false: ハッシュタグの書式になっていない場合
   */
  private def formattedAsHashTag(maybeHashTag: String): Boolean = {
    val matches = """#[[^\s]&&[^\p{Punct}]]+""".r.findAllMatchIn(maybeHashTag)

    matches.size == 1
  }
}
