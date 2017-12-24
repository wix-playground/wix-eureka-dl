package com.wix.eureka.dl.engines.spark

import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

class SparkDataTest extends SpecificationWithJUnit with Mockito {

  "The SparkData" should {

    import Spark._

    val testFilename = Spark.filename("test", "correlations")
    val testFile = Spark.read(testFilename)

    "filename" in {
      Spark.filename("name", "folder", "lang") must_== "/models/folder/name_lang.txt"
    }

    "read" in {
      testFile.size must_== 7
    }

    "map" in {
      Spark.map(testFile, (s) => s.asVectorDoubles()).size must_== testFile.size
    }

    "reduce" in {
      Spark.reduce(testFile, (s) => s.asVectorDoubles(), (v) => v).count() must_== testFile.size
    }

    "split" in {
      Spark.split(Spark.reduce(testFile, (s) => s.asVectorDoubles(), (v) => v), 0.5).size must_== 2
    }
  }
}
