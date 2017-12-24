package com.wix.eureka.dl.engines.spark

import com.wix.one.common.helpers.NumberUtils
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD

/**
  * Created by Yuval_Aviyam on 24/8/2017.
  */
trait SparkUtils extends NumberUtils {

  private val hashingTF = new HashingTF(10000)

  implicit class VectorStringConverters(val s: String) {
    def asVectorStrings(sep: String = "\t"): Vector =
      s.split(sep).toSeq.asVector

    def asVectorDoubles(sep: String = "\t"): Vector =
      s.split(sep).toSeq.map(_.toDoubleOrZero).asVector
  }

  implicit class VectorSeqStringsConverters(val seq: Seq[String]) {
    def asVector: Vector =
      hashingTF.transform(seq.map(_.toLowerCase))
  }

  implicit class VectorSeqDoublesConverters(val seq: Seq[Double]) {
    def asVector: Vector =
      Vectors.dense(seq.toArray)
  }

  implicit class VectorSeqIntsConverters(val seq: Seq[Int]) {
    def asVector: Vector =
      seq.map(_.toDouble).asVector
  }

  implicit class VectorRDDDoublesConverters(val rdd: RDD[Double]) {
    def asVector: Vector =
      rdd.collect().toSeq.asVector
  }
}
