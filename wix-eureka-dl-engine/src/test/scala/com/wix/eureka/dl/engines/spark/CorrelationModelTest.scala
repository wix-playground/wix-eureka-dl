package com.wix.eureka.dl.engines.spark

import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

/**
  * Created by Yuval_Aviyam on 27/8/2017.
  */
class CorrelationModelTest extends SpecificationWithJUnit with Mockito with CorrelationUtils {

  val model = CorrelationModel("test", "correlations")

  "The CorrelationModel" should {

    sequential

    "build correlation model" in {
      model.getField(0) must_== "id"
      model.getField(1) must_== "is_sell"
      model.getField(2) must_== "fld_2"
      model.getField(3) must_== "fld_3"
      model.getField(4) must_== "fld_4"
      model.getField(5) must_== "facebook_likes"
    }

    "get correlations" in {
      val correlations = model.getCorrelations(1)
      correlations.getCorrelation(2).correlation must_== -1
      correlations.getCorrelation(3).correlation must_== -0.447
      correlations.getCorrelation(4).correlation must_== 0.333
      correlations.getCorrelation(5).correlation must_== 0.925
    }

    "calculate correlations" in {
      val scores = model.calculateCorrelations(1, Seq("item-test", 1, -1, 1, 1, 1024))
      scores.getCorrelation(2).score must_== 1.0
      scores.getCorrelation(3).score must_== 0.551
      scores.getCorrelation(4).score must_== 0.41
      scores.getCorrelation(5).score must_== 0.505
    }
  }
}