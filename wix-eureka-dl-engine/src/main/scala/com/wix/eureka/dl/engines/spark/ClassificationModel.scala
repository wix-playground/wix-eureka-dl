package com.wix.eureka.dl.engines.spark

import com.wix.eureka.dl.domain.ClassificationAlgorithm
import com.wix.eureka.dl.domain.ClassificationAlgorithm.ClassificationAlgorithm
import org.apache.spark.mllib.classification._
import org.apache.spark.mllib.evaluation.{BinaryClassificationMetrics, MulticlassMetrics}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.rdd.RDD

class ClassificationModel(folder: String, groups: Map[String, Double], language: String, algorithm: ClassificationAlgorithm, testWeight: Double, threshold: Double) {

  import Spark._

  val name: String = algorithm + "/" + (if (groups.size == 1) groups.keys.head else folder)

  // https://spark.apache.org/docs/latest/mllib-linear-methods.html#classification

  private val model = trainModel()

  def status: String =
    s"model [$name] loaded -> [${ClassificationModel.toParams(model)}]"

  private def trainModel() = profile2 {
    val data = reduce(groups.flatMap { case (k, v) => {
      val rows = read(filename(k, folder, language))
      reduce(rows, (s) => s.toWords.asVector, (f) => LabeledPoint(v, f)).collect()
    } }.toSeq)

    /*
    val (trainData, testData) = if (testWeight > 0) {
      val splits = split(data, testWeight)
      (splits(0).cache(), splits(1))
    } else (data.cache(), null)
    */
    val Array(trainData, testData) = split(data, testWeight)

    val model = algorithm match {
      case ClassificationAlgorithm.LogisticRegression => LogisticRegressionWithSGD.train(trainData, 100, 1.0, 1.0)
      case ClassificationAlgorithm.DecisionTree => DecisionTree.trainClassifier(trainData, 2, Map[Int, Int](), "gini", 5, 32)
      case ClassificationAlgorithm.SVM => SVMWithSGD.train(trainData, 100)
      case ClassificationAlgorithm.NaiveBayes => NaiveBayes.train(trainData, 1.0, "multinomial")
      case _ => null
    }

    if (testWeight > 0) model match {
      case m: LogisticRegressionModel => testModel(m, testData, threshold, "test")
      case m: DecisionTreeModel => testModel(m, testData, "DecisionTree")
      case m: SVMModel => testModel(m, testData, "SVM")
      case m: NaiveBayesModel => testModel(m, testData, "NaiveBayes")
      case _ =>
    }

    // groups.foreach { case (k, v) => testModel(model, reduce(data.filter(_.label == v).takeSample(false, 10)), k) }

    model
  } match {
    case Right((result, time)) => {
      info(s"trainModel[$name]", ClassificationModel.toParams(result), time)
      result
    }
    case Left((e, time)) => {
      error(s"trainModel[$name]", e, time)
      throw e
    }
  }

  def predict(text: String): Double = profile2 {
    predict(text.toWords.asVector)
  } match {
    case Right((result, time)) => {
      info(s"predict[$name] [$text]", result, time)
      result
    }
    case Left((e, time)) => {
      error(s"predict[$name] [$text]", e, time)
      -1
    }
  }

  private def predict(features: Vector): Double = model match {
    case m: LogisticRegressionModel => m.predict(features)
    case m: DecisionTreeModel => m.predict(features)
    case m: SVMModel => m.predict(features)
    case m: NaiveBayesModel => m.predict(features)
    case _ => -1
  }

  def classify(text: String): Option[String] = {
    val prediction = predict(text)
    groups.find { case (_, v) => v == prediction } map { case (k, _) => k }
  }

  private def testModel(model: LogisticRegressionModel, data: RDD[LabeledPoint], threshold: Double, identifier: String): Unit = profile2 {
    if (threshold > 0)
      model.setThreshold(threshold)
    else model.clearThreshold()
    val predictions = data.map { item => (model.predict(item.features), item.label) }
    new MulticlassMetrics(predictions)
  } match {
    case Right((result, time)) => info(s"testModel[$name] [$identifier] [threshold = $threshold]", ClassificationModel.toStats(result), time)
    case Left((e, time)) => error(s"testModel[$name] [$identifier] [threshold = $threshold]", e, time)
  }

  private def testModel(model: DecisionTreeModel, data: RDD[LabeledPoint], identifier: String): Unit = profile2 {
    val predictions = data.map { item => (model.predict(item.features), item.label) }
    new MulticlassMetrics(predictions)
  } match {
    case Right((result, time)) => info(s"testModel[$name] [$identifier] [threshold = $threshold]", ClassificationModel.toStats(result), time)
    case Left((e, time)) => error(s"testModel[$name] [$identifier] [threshold = $threshold]", e, time)
  }

  private def testModel(model: SVMModel, data: RDD[LabeledPoint], identifier: String): Unit = profile2 {
    val predictions = data.map { item => (model.predict(item.features), item.label) }
    new BinaryClassificationMetrics(predictions)
  } match {
    case Right((result, time)) => info(s"testModel[$name] [$identifier] [threshold = $threshold]", ClassificationModel.toStats(result), time)
    case Left((e, time)) => error(s"testModel[$name] [$identifier] [threshold = $threshold]", e, time)
  }

  private def testModel(model: NaiveBayesModel, data: RDD[LabeledPoint], identifier: String): Unit = profile2 {
    val predictions = data.map { item => (model.predict(item.features), item.label) }
    new MulticlassMetrics(predictions)
  } match {
    case Right((result, time)) => info(s"testModel[$name] [$identifier] [threshold = $threshold]", ClassificationModel.toStats(result), time)
    case Left((e, time)) => error(s"testModel[$name] [$identifier] [threshold = $threshold]", e, time)
  }

  /*
  private def save(): Unit = try {
    model.save(sparkContext, "~/tmp/model")
    val sameModel = LogisticRegressionModel.load(sparkContext, "~/tmp/model")
  } catch {
    case e: Exception => println("ERROR: " + e.getMessage)
  }


  private def load(): Unit = try {
    LogisticRegressionModel.load(sparkContext, "~/tmp/model")
  } catch {
    case e: Exception => println("ERROR: " + e.getMessage)
  }
  */
}

object ClassificationModel {

  import Spark._

  private val defaultGroups: Map[String, Double] = Map("positive" -> 1.0, "negative" -> 0.0)

  def apply(folder: String, groups: Map[String, Double] = defaultGroups, language: String = "en", algorithm: Option[ClassificationAlgorithm] = None, testWeight: Double = 0, threshold: Double = 0.5): ClassificationModel =
    new ClassificationModel(folder, groups, language, algorithm.getOrElse(if (groups.size > 1) ClassificationAlgorithm.NaiveBayes else ClassificationAlgorithm.SVM), testWeight, threshold)

  def toParams(model: Any): String =
    stringifyMap(params(model))

  def params(model: Any): Map[String, Any] = model match {
    case m: LogisticRegressionModel => params(m)
    case m: DecisionTreeModel => params(m)
    case m: SVMModel => params(m)
    case m: NaiveBayesModel => params(m)
    case _ => Map.empty
  }

  def params(model: LogisticRegressionModel): Map[String, Any] =
    Map("classes" -> model.numClasses, "features" -> model.numFeatures)

  def params(model: DecisionTreeModel): Map[String, Any] =
    Map("nodes" -> model.numNodes, "depth" -> model.depth, "algo" -> model.algo)

  def params(model: SVMModel): Map[String, Any] =
    Map("intercept" -> model.intercept, "weights" -> model.weights.size)

  def params(model: NaiveBayesModel): Map[String, Any] =
    Map("type" -> model.modelType, "labels" -> model.labels.length)

  def toStats(metrics: Any): String =
    stringifyMap(stats(metrics))

  def stats(metrics: Any): Map[String, Any] = metrics match {
    case m: MulticlassMetrics => stats(m)
    case m: BinaryClassificationMetrics => stats(m)
    case _ => Map.empty
  }

  def stats(metrics: MulticlassMetrics): Map[String, Any] =
    Map("accuracy" -> metrics.accuracy, "precision" -> metrics.weightedPrecision, "recall" -> metrics.weightedRecall)

  def stats(metrics: BinaryClassificationMetrics): Map[String, Any] =
    Map("rpc" -> metrics.areaUnderROC, "pr" -> metrics.areaUnderPR)
}

