package com.wix.eureka.dl.engines.spark

import org.apache.spark.{SparkConf, SparkContext}

trait SparkInstance {

  import org.apache.log4j.{Level, Logger}

  def setLogLevels(): Unit =
    setLogLevels(Level.OFF, Seq("specs2", "org", "spark", "jetty", "akka"))

  def setLogLevels(level: Level, loggers: TraversableOnce[String]): Unit =
    loggers.foreach(Logger.getLogger(_).setLevel(level))

  // https://spark.apache.org/docs/latest/submitting-applications.html

  private[this] val sparkConf = new SparkConf(false)
    .setMaster("local[*]")
    .setAppName("eureka-dl")
    .set("spark.driver.memory", "2g")
    .set("spark.executor.memory", "2g")
    .set("spark.ui.enabled", "false")
  //.set("spark.local.ip","localhost")
  //.set("spark.driver.host","localhost")

  implicit val sparkContext: SparkContext = new SparkContext(sparkConf)
  //implicit val sparkSession: SparkSession = SparkSession.builder().appName("Eureka DL").config(sparkConf).getOrCreate()

  sparkContext.setLogLevel("OFF")
  setLogLevels()
}
