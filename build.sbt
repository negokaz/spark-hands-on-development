/** 各プロジェクト名のプレフィックス */
lazy val namePrefix = "twitter-hashtag-ranking"

/**
 * 全プロジェクト共通の設定
 */
lazy val commonSettings = Seq(

  version := "1.0.0",

  // Spark のデフォルト(2.10)に合わせておく
  scalaVersion := "2.10.5"
)

/** 利用する Spark のバージョン */
lazy val sparkVersion = "1.5.1"

/** 利用する Akka のバージョン */
lazy val akkaVersion = "2.3.13"

/**
 * フロントエンドのプロジェクト設定
 */
lazy val root = (project in file(".")).
  enablePlugins(PlayScala).
  settings(commonSettings: _*).
  settings(

    name := s"$namePrefix-frontend",

    libraryDependencies ++= Seq(
      // バックエンドとは Akka を使って情報をやりとりする
      "com.typesafe.akka" %% "akka-actor"  % akkaVersion,
      "com.typesafe.akka" %% "akka-remote" % akkaVersion
    ),

    // Play provides two styles of routers, one expects its actions to be injected, the
    // other, legacy style, accesses its actions statically.
    routesGenerator := InjectedRoutesGenerator
  ).
  dependsOn(
    backendApi
  )

/**
 * バックエンドのAPIを定義したプロジェクトの設定
 */
lazy val backendApi = (project in file("modules/backend-api")).
  settings(commonSettings: _*).
  settings(

    name := s"$namePrefix-backend-api"
  )

/**
 * バックエンドのプロジェクト設定
 */
lazy val backend = (project in file("modules/backend")).
  settings(commonSettings: _*).
  settings(

    name := s"$namePrefix-backend",

    libraryDependencies ++= Seq(
      // Spark への依存関係
      "org.apache.spark"  %% "spark-core"              % sparkVersion,
      // Spark Streaming への依存関係
      "org.apache.spark"  %% "spark-streaming"         % sparkVersion,
      // Spark Streaming で Twitter のデータを取得するためのライブラリ
      "org.apache.spark"  %% "spark-streaming-twitter" % sparkVersion,
      // フロントエンドとは Akka を使って情報をやりとりする
      "com.typesafe.akka" %% "akka-actor"              % akkaVersion,
      "com.typesafe.akka" %% "akka-remote"             % akkaVersion
    ),

    fullRunInputTask(run, Compile, backendMainClass, "local[4]"),
    fullRunTask(collectTweets, Compile, "com.example.tagrank.backend.TweetCollector"),
    fullRunTask(print, Compile, "com.example.tagrank.backend.Debugger"),

    mainClass in assembly := Some(backendMainClass),

    /**
     * assembly したときに発生するエラーを回避するための設定
     * 参考: http://stackoverflow.com/questions/30446984/spark-sbt-assembly-deduplicate-different-file-contents-found-in-the-followi
     */
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
      case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
      case PathList("org", "apache", xs @ _*) => MergeStrategy.last
      case PathList("com", "google", xs @ _*) => MergeStrategy.last
      case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
      case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
      case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
      case "about.html" => MergeStrategy.rename
      case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
      case "META-INF/mailcap" => MergeStrategy.last
      case "META-INF/mimetypes.default" => MergeStrategy.last
      case "plugin.properties" => MergeStrategy.last
      case "log4j.properties" => MergeStrategy.last
      case "log4j.xml" => MergeStrategy.last
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  ).
  dependsOn(
    backendApi
  )

lazy val backendMainClass = "com.example.tagrank.backend.Backend"

lazy val collectTweets = taskKey[Unit]("Tweetをファイルに保存します")
lazy val print = taskKey[Unit]("標準出力に結果を表示します")
