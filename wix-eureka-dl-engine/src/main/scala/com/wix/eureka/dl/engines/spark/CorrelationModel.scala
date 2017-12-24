package com.wix.eureka.dl.engines.spark

import com.wix.eureka.dl.domain.CorrelationAlgorithm.CorrelationAlgorithm
import com.wix.eureka.dl.domain.{CorrelationAlgorithm, CorrelationField, CorrelationScore}
import com.wix.eureka.dl.engines.spark.Spark.{filename, read, reduce, stringifyMap}
import org.apache.spark.mllib.linalg.{Matrix, Vector}
import org.apache.spark.mllib.stat.{Statistics => stat}
import org.apache.spark.rdd.RDD

// https://people.apache.org/~pwendell/spark-nightly/spark-master-docs/latest/ml-statistics.html

class CorrelationModel(name: String, data: RDD[Vector], fields: Seq[String], algorithm: CorrelationAlgorithm) extends CorrelationUtils {

  import Spark._

  private val matrix = buildCorrelations

  def status: String =
    s"model [$name] loaded -> [${CorrelationModel.toParams(matrix)}]"

  def getFields: Seq[String] = fields

  def getField(idx: Int): String =
    fields.drop(idx).headOption.getOrElse(s"field_$idx")

  def getMatrix: Seq[Seq[Double]] =
    matrix.transpose.toArray.grouped(matrix.numCols).toList.map(_.toSeq)

  private def getFieldIdx(name: String): Int =
    fields.zipWithIndex
      .find { case (f, _) => f equalsIgnoreCase name }.map { case (_, idx) => idx }
      .getOrElse(-1)

  private def columns = (0 to matrix.numCols - 1)

  private def buildCorrelations = profile2 {
    stat.corr(data, algorithm.toString)
  } match {
    case Right((result, time)) => {
      info(s"buildCorrelations[$name]", CorrelationModel.toParams(result), time)
      result
    }
    case Left((e, time)) => {
      error(s"buildCorrelations[$name]", e, time)
      throw e
    }
  }

  def getCorrelation(a: Int, b: Int): CorrelationField = {
    val (mean, stdev) = calculateCorrelationStats(data.collect().map(_(b)))
    CorrelationField(b, getField(b), matrix.toArray(b + a * matrix.numCols).rounded(3), mean, stdev)
  }

  def getCorrelation(a: String, b: String): CorrelationField =
    getCorrelation(getFieldIdx(a), getFieldIdx(b))

  def getCorrelations(idx: Int): List[CorrelationField] =
    columns.map(getCorrelation(idx, _)).toList.sort

  def getCorrelations(name: String): List[CorrelationField] =
    getCorrelations(getFieldIdx(name))

  /*
  def getCorrelations(column: Int): Map[Int, Double] =
    (0 to matrix.numCols - 1)/*.filterNot(_ == column)*/.map(i => i -> matrix.toArray(i + column * matrix.numCols).rounded(3)).toMap
  */

  def calculateCorrelations(column: Int, row: Seq[Any]): List[CorrelationScore] = {
    val columnValue = row(column).toString.toDoubleOrZero
    val pos = data.collect().filter(r => r(column) == columnValue)
    //val neg = data.collect().filter(r => r(column) != columnValue)
    val correlations = getCorrelations(column)
    (0 to matrix.numCols - 1).map(i => {
      val c = correlations.getCorrelation(i)
      val v = row(i).toString.toDoubleOrZero
      val (mean, stdev) = calculateCorrelationStats(pos.map(_(i)))
      CorrelationScore(c.withStats(mean, stdev), v)
    }).sortWith((a, b) => a.field.idx < b.field.idx).toList
  }

  private def calculateCorrelationStats(values: Seq[Double]): (Double, Double) = {
    val stats = reduce(values).stats()
    (stats.mean.rounded(3), stats.stdev.rounded(3))
  }

  def withFilter(f: (Vector) => Boolean): CorrelationModel =
    new CorrelationModel(name, reduce(filter(data.collect(), f)), fields, algorithm)

  def dump(): Unit = {
    //val cells = Seq(Seq("") ++ getFields) ++ getFields.map(f => Seq(f) ++ getCorrelations(f).map(_.correlation.toString))
    val cells = Seq(Seq("") ++ getFields) ++ columns.map(i => Seq(getField(i)) ++ getCorrelations(i).map(c => if (c.idx != i) c.correlation.toString else ""))
    println("================================================================================")
    println(cells.map(_.mkString("\t")).mkString("\r\n"))
    println("================================================================================")
  }

  def save(): Unit = {
    val text = (fields.mkString("\t") :+ getMatrix.map(_.mkString("\t"))).mkString("\r\n")
  }
}

object CorrelationModel extends SparkUtils {

  def apply(name: String, folder: String = ""): CorrelationModel = {
    val rows = read(filename(name, folder))
    val header = rows.head.split('\t').map(_.trim)
    val data = reduce(rows.drop(1), (s) => s.asVectorDoubles(), (f) => f)
    new CorrelationModel(folder, data, header, CorrelationAlgorithm.pearson)
  }

  def apply(name: String, items: Seq[Seq[Int]], fields: Seq[String]): CorrelationModel = {
    val data = reduce(items.map(_.asVector))
    new CorrelationModel(name, data, fields, CorrelationAlgorithm.pearson)
  }

  def toParams(matrix: Matrix): String =
    stringifyMap(params(matrix))

  def params(matrix: Matrix): Map[String, Any] =
    Map("correlations" -> matrix.numRows * (matrix.numCols - 1))
}

trait CorrelationUtils {

  implicit class CorrelationFields(val correlations: List[CorrelationField]) {
    def getCorrelation(idx: Int): CorrelationField =
      correlations.find(_.idx == idx).getOrElse(CorrelationField.EMPTY)

    def getCorrelation(name: String): CorrelationField =
      correlations.find(_.name equalsIgnoreCase name).getOrElse(CorrelationField.EMPTY)

    def sort: List[CorrelationField] =
      correlations.sortBy(_.idx)
  }

  implicit class CorrelationScores(val correlations: List[CorrelationScore]) {
    def getCorrelation(idx: Int): CorrelationScore =
      correlations.find(_.field.idx == idx).getOrElse(CorrelationScore.EMPTY)

    def getCorrelation(name: String): CorrelationScore =
      correlations.find(_.field.name equalsIgnoreCase name).getOrElse(CorrelationScore.EMPTY)
  }
}
