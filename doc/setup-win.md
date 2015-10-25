# 事前準備 (Windows 編)

ハンズオン当日までにこのページの手順に従って準備をお願いします。
準備時間のめやすは<?>時間です。

* TwitterのAPIキーを取得
* 開発環境構築
* Scalaの基本を理解

## 困ったときは

事前準備でわからないことがあったり、トラブルが起きた場合は [Issues](https://github.com/negokaz/spark-hands-on-development/issues) から連絡をお願いします。

## TwitterのAPIキーを取得

下記のページを参考に、合計4つのAPIキーを取得します。

参考: [Twitter APIの使い方まとめ](https://syncer.jp/twitter-api-matome)

[アプリケーションの登録](https://syncer.jp/twitter-api-matome#sec-1) の手順で、下記2つのキーを取得しメモしておいてください。

* Consumer Key (API Key)
* Consumer Secret (API Secret)

[アクセストークンの取得 - プログラミングなしで取得する](https://syncer.jp/twitter-api-matome#sec-2-2) の手順で、下記2つのキーを取得しメモしておいてください。

* Access Token
* Access Token Secret

## 開発環境構築

### 各種ソフトウェアのインストール

#### Git

下記のページを参考にしてインストールしてください。
*グローバルの設定* 以降は任意です。

参考: [私家版 Git For Windowsのインストール手順](http://opcdiary.net/?page_id=27065)

インストール後、下記のコマンドを実行してバージョンが表示されることを確認してください。
(バージョン番号が異なっていても問題ありません)

`git --version`
~~~
git version 2.2.1
~~~

#### JDK

下記のページを参考にしてインストールしてください。
PATH と JAVA_HOME を設定するところまで実施してください。

参考: [Java(JDK)ダウンロードとインストール](http://www.javadrive.jp/install/)

インストール後、下記のコマンドを実行してバージョンが表示されることを確認してください。
(バージョン番号が 1.8 から始まっていれば問題ありません)

`javac -version`
~~~
javac 1.8.0_45
~~~

#### Apache Spark 1.5.1

http://spark.apache.org/downloads.html からダウンロードします。

* Choose a Spark release: 1.5.1
* Choose a package type: Pre-build for Hadoop 2.6 and later

を選択して *spark-1.5.1-bin-hadoop2.6.tgz* をクリックしてください。

![Sparkダウンロード](download-spark.png)

ミラーリンクの一覧が表示されるので、一番上に表示されるミラーリンクを選択し、ファイルをダウンロードします。

![ミラーリンクの選択](select-spark-mirror.png)

tgz ファイルを解凍し、解凍されたディレクトリを任意の場所に配置します。
解凍したディレクトリの **bin** ディレクトリのパスを環境変数 *PATH* に追加し、パスを通しておきます。

---

※ 環境変数の設定はJDKのときと同じ方法で設定できます。

---

インストール後、下記のコマンドを実行してバージョンが **1.5.1** になっていることを確認してください。

`spark-shell --version`
~~~
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 1.5.1
      /_/

Type --help for more information.
~~~

#### Hadoop 2.6

[Hadoop 2.6.0 Windows 64-bit Binaries](http://www.barik.net/archive/2015/01/19/172716/) から *hadoop-2.6.0.tar.gz* をダウンロードします。

tgz ファイルを解凍し、解凍されたディレクトリを任意の場所に配置します。
解凍したディレクトリのパスを環境変数 *HADOOP_HOME* として設定します。

インストール後、下記のコマンドを実行して `%HADOOP_HOME%\bin\winutils.exe` が存在することを確認してください。

`dir %HADOOP_HOME%\bin\winutils.exe /b`
~~~
winutils.exe
~~~

---

※ これは Windows でのみ必要な手順です #2

参考: [Apache SparkをTypesafe Activatorから試す #Windowsにはwinutils.exeが必要](http://qiita.com/pyr_revs/items/9bd4a1ef3f40a2f7a804#windows%E3%81%AB%E3%81%AFwinutilsexe%E3%81%8C%E5%BF%85%E8%A6%81)

---

#### IntelliJ IDEA 14

[公式のダウンロードページ](https://www.jetbrains.com/idea/download/) から無料の Community Edition をダウンロードしてください。
![Community Edition を選択](download-idea.png)

ダウンロードされた exe ファイルを起動し、ウィザードに従ってインストールしてください。

### プロジェクトをPCにクローン

ターミナルから任意のディレクトリで下記のコマンドを実行してください。
`git clone https://github.com/negokaz/spark-hands-on-development.git`

現在のディレクトリ配下に spark-hands-on-development というディレクトリができていることを確認してください。

### IntelliJ IDEA に Scala Plugin をインストール

下記ページを参考にして IntelliJ IDEA に Scala Plugin をインストールしてください。
*ScalaのためのIntelliJ IDEAの初期設定* の章のJDKを設定するところまででOKです。

参考: [Scala 開発環境構築(IntelliJ IDEA, SBT, scalaenv)](http://blog.chopschips.net/blog/2014/09/07/scala-get-started/)


### IntelliJ IDEA へプロジェクトをインポート

IntelliJ IDEA を起動し、 Import Project からプロジェクトをインポートします。

![Import Project を選択](idea-import.png)

インポートするプロジェクトを選択するときは Scala のプロジェクトと認識させるために spark-hands-on-development のディレクトリの配下にある **build.sbt** を選択して OK を押下します。

![インポートするプロジェクトの選択](idea-import-select.png)

インポートの設定画面が表示されたら、Project SDK に 1.8 の SDK を指定します。

![インポートの設定](idea-import-setting.png)

### TwitterのAPIキーを設定

IntelliJ IDEA 上で `spark-hands-on-development/modules/backend/src/main/resources/application.conf` を開き、下記の部分に事前に取得したTwitterのAPIキーをそれぞれ書き込んでください。
(ダブルクオートの間にペーストしてください)
~~~
twitter-hashtag-ranking {

  twitter.oauth {
    // Consumer Key (API Key)
    consumerKey = ""
    // Consumer Secret (API Secret)
    consumerSecret = ""
    // Access Token
    accessToken = ""
    // Access Token Secret
    accessTokenSecret = ""
  }
}

~~~

### プロジェクトの起動確認

IntelliJ IDEA のターミナルから下記のコマンドを実行してバックエンドサーバーを起動します。

`activator backend/run`

同じように下記のコマンドを実行してフロントエンドサーバーを起動します。

`activator run`

----

※ IntelliJ IDEA のターミナルは "Terminal" と書かれたアイコンを押すと開くことができます。
ターミナルを複数起動したい場合は "+" のアイコンを押すとタブが追加されます。
![ターミナル](idea-terminal.png)

---

ブラウザで `http://localhost:9000` にアクセスして下のような画面が表示されることを確認してください。

![画面](screenshot.png)

## Scalaの基本を理解

以下の項目を押さえておいて頂くとハンズオンの内容理解がしやすくなると思います。

* 変数の定義
  * val
  * var
* タプル
* ケースクラス
* メソッドの定義
* メソッドの呼び出し
* 関数リテラル
  * PartialFunction (case)
* コードの省略ルール
* コレクションの高階関数
   * filter
   * map
   * flatMap

### 参考資料

* [Scala School意訳(Basics)](http://seratch.hatenablog.jp/entry/20111101/1320155723)
* [Scala の省略ルール早覚え](https://gist.github.com/gakuzzzz/10104162)
* [Scala入門ハンズオン](https://github.com/bati11/learn-scala/tree/master/getting_started)
* [たのしい高階関数](http://www.slideshare.net/s_kozake/ss-15327269)
* [flatMapをマスターする](http://qiita.com/mtoyoshi/items/c95cc88de2910945c39d)
