package com.wix.eureka.dl.engines.spark

import com.wix.one.common.helpers.{ResourceUtils, StringUtils}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

trait SparkData extends SparkInstance with SparkUtils with StringUtils with ResourceUtils {

  // https://github.com/vaquarkhan/vk-wiki-notes/wiki/Apache-Spark-SQL
  
  def filename(name: String, folder: String = "", language: String = ""): String =
    Seq("models", folder, name + (if (language.isDefined) s"_$language" else "")).filter(_.isDefined).mkString("/", "/", ".txt")

  def read(filename: String): Seq[String] =
    readResource(filename)

  def save[T](filename: String, seq: Seq[T])(implicit ev: ClassTag[T]): Unit =
    sparkContext.parallelize(seq).saveAsTextFile(filename)

  def filter(vectors: Seq[Vector], f: (Vector) => Boolean): Seq[Vector] =
    vectors.filter(f(_))

  def map(rows: Seq[String], s: (String) => Vector): Seq[Vector] =
    rows.map(s(_))

  def reduce[T](rows: Seq[String], s: (String) => Vector, v: (Vector) => T)(implicit ev: ClassTag[T]): RDD[T] =
    reduce(map(rows, s), v)

  def reduce[T](vectors: Seq[Vector], v: (Vector) => T)(implicit ev: ClassTag[T]): RDD[T] =
    reduce(vectors.map(v(_)))

  def reduce[T](rows: Seq[T])(implicit ev: ClassTag[T]): RDD[T] =
    sparkContext.parallelize(rows).cache()
    //sparkSession.sparkContext.parallelize(rows).cache() //sqlContext.sparkContext.parallelize(rows).cache()

  def split[T](rdd: RDD[T], testWeight: Double = 0.2, seed: Long = 10): Array[RDD[T]] =
    rdd.randomSplit(Array(1 - testWeight, testWeight), seed).map(_.cache())
}

