package com.wix.eureka.dl.domain

import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

/**
  * Created by Yuval_Aviyam on 23/8/2017.
  */
class CorrelationScoreTest extends SpecificationWithJUnit with Mockito {

  "The CorrelationScore" should {

    import CorrelationScore._

    "calculate score" in {
      calculateScore(10, 50, 25) must_== 0.018
      calculateScore(20, 50, 25) must_== 0.104
      calculateScore(30, 50, 25) must_== 0.366
      calculateScore(40, 50, 25) must_== 0.778
      calculateScore(50, 50, 25) must_== 1.0
      calculateScore(60, 50, 25) must_== 0.778
      calculateScore(70, 50, 25) must_== 0.366
      calculateScore(80, 50, 25) must_== 0.104
      calculateScore(90, 50, 25) must_== 0.018
    }

    "score must be equal" in {
      CorrelationScore(900, 0.5, 800, 200).scoreValueEqual must_== 0.26
      CorrelationScore(900, 0.5, 1000, 200).scoreValueEqual must_== 0.26
    }

    "score must be lower" in {
      CorrelationScore(900, 0.5, 800, 200).scoreValueLower must_== 1
      CorrelationScore(900, 0.5, 1000, 200).scoreValueLower must_== 0.26
    }

    "score must be greater" in {
      CorrelationScore(900, 0.5, 800, 200).scoreValueGreater must_== 0.26
      CorrelationScore(900, 0.5, 1000, 200).scoreValueGreater must_== 1
    }
  }
}