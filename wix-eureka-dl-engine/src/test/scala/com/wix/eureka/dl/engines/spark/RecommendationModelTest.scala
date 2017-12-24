package com.wix.eureka.dl.engines.spark

import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

/**
  * Created by Yuval_Aviyam on 27/8/2017.
  */
class RecommendationModelTest extends SpecificationWithJUnit with Mockito with CorrelationUtils {

  val model = RecommendationModel("recommendation", "ratings")

  "The RecommendationModel" should {

    sequential

    "predict with trained rating" in pending /*{
      model.predict(1, 1) must_== 5
    }*/

    "predict new rating" in {
      model.predict(1, 100) must beGreaterThan(3.0)
    }

    "recommend" in {
      model.recommend(1, 10).size must beGreaterThan(0)
    }

    "recommend all" in {
      model.recommendAll(10).size must_== model.countUsers
    }
  }
}