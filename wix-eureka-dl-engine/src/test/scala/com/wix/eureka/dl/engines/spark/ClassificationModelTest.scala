package com.wix.eureka.dl.engines.spark

import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

/**
  * Created by Yuval_Aviyam on 27/8/2017.
  */
class ClassificationModelTest extends SpecificationWithJUnit with Mockito with CorrelationUtils {

  val model = ClassificationModel("classification")

  "The ClassificationModel" should {

    sequential

    "classify" in {
      model.classify("b") must_== Some("positive")
      model.classify("x") must_== Some("negative")
      model.classify("m") must_== Some("negative")
    }

    "predict" in {
      model.predict("a") must_== 1
      model.predict("z") must_== 0
      model.predict("m") must_== 0
    }
  }
}