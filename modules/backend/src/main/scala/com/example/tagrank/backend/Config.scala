package com.example.tagrank.backend

import twitter4j.auth.{AccessToken, Authorization}

trait Config {

  val config: com.typesafe.config.Config

  lazy val twitterConsumerKey = config.getString("twitter-hashtag-ranking.twitter.oauth.consumerKey")

  lazy val twitterConsumerSecret = config.getString("twitter-hashtag-ranking.twitter.oauth.consumerSecret")

  lazy val twitterAccessToken = config.getString("twitter-hashtag-ranking.twitter.oauth.accessToken")

  lazy val twitterAccessTokenSecret = config.getString("twitter-hashtag-ranking.twitter.oauth.accessTokenSecret")
}
