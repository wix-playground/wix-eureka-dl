package com.wix.eureka.dl.engines.spark

import com.wix.one.common.helpers.StringUtils
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD

// https://spark.apache.org/docs/2.2.0/mllib-collaborative-filtering.html

class RecommendationModel(folder: String, data: RDD[Rating]) extends StringUtils {

  import Spark._

  val name: String = folder

  val countRatings = data.count()

  val countUsers = data.map(_.user).distinct.count()

  val countProducts = data.map(_.product).distinct.count()

  val model = trainModel()

  def status: String =
    s"model [$name] loaded -> [${RecommendationModel.toParams(model)}]"

  private def trainModel() = profile2 {
    val Array(trainData, testData) = split(data, 0.02)
    val model = ALS.train(trainData, 5, 5, 0.01)
    testModel(model, testData, 0, "test")
    model
  } match {
    case Right((result, time)) => {
      info(s"trainModel[$name]", RecommendationModel.toParams(result), time)
      result
    }
    case Left((e, time)) => {
      error(s"trainModel[$name]", e, time)
      throw e
    }
  }

  private def testModel(model: MatrixFactorizationModel, data: RDD[Rating], threshold: Double, identifier: String): Unit = profile2 {
    // https://gist.github.com/hiepph/567370a5d7a5ef2093939631cb416f26
    // https://community.mapr.com/community/exchange/blog/2017/07/05/churn-prediction-with-apache-spark-machine-learning

    val usersProducts = data.map { case Rating(user, product, _) => (user, product) }
    val predictions = model.predict(usersProducts).map { case Rating(user, product, rate) => ((user, product), rate) }
    val ratesAndPredictions = data.map { case Rating(user, product, rate) => ((user, product), rate) }.join(predictions)
    val meanSquaredError = ratesAndPredictions.map { case ((_, _), (rt, rp)) => math.pow(rt - rp, 2) }.mean().rounded(3)
    val falsePositives = ratesAndPredictions.filter { case ((_, _), (rt, rp)) => (rt <= 1 && rp >= 4) }.count().rounded(3)

    s"meanSquaredError = $meanSquaredError , falsePositives = $falsePositives"
  } match {
    case Right((result, time)) => info(s"testModel[$name] [$identifier]", result, time)
    case Left((e, time)) => error(s"testModel[$name] [$identifier]", e, time)
  }

//  def recommend(): Map[Int, Array[Rating]] = {
//    val rec = model.recommendProductsForUsers(2)
//
//    println("### id=" + rec.id + " name=" + rec.name + " count=" + rec.count())
//
//    rec.takeSample(false, 10).foreach { case (id, a) => {
//      println("### user=" + id)
//
//      val userRatings = data.filter(r => r.user == id)
//
//      println("  ratings:")
//      userRatings.foreach(r => println("    product=" + r.product + " rating=" + r.rating))
//
//      println("  recommendations:")
//      a.foreach(r => println("    product=" + r.product + " rating=" + r.rating))
//
//      println("----------")
//    }
//    }
//
//    rec.collectAsMap().toMap
//  }

  def predict(user: Int, product: Int): Double = {
    model.predict(user, product)
  }

  def recommend(user: Int, count: Int): Array[Rating] =
    model.recommendProducts(user, count)

  def recommendAll(count: Int): Map[Int, Array[Rating]] =
    model.recommendProductsForUsers(count).collectAsMap().toMap
}

object RecommendationModel extends SparkUtils {

  import Spark._

  def apply(folder: String, name: String): RecommendationModel = {
    val rows = read(filename(name, folder))
    val header = rows.head.split('\t').map(_.trim)
    val data = reduce(rows.drop(1), (s) => s.asVectorDoubles(), (f) => Rating(f(0).toInt, f(1).toInt, f(2)))
    new RecommendationModel(folder, data)
  }

  def toParams(model: MatrixFactorizationModel): String =
    stringifyMap(params(model))

  def params(model: MatrixFactorizationModel): Map[String, Any] =
    Map("users" -> model.userFeatures.count(), "products" -> model.productFeatures.count(), "rank" -> model.rank)
}
