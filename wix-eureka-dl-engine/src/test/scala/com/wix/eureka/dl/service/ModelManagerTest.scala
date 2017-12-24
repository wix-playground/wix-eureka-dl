package com.wix.eureka.dl.service

import com.wix.eureka.config.BaseExecutorMocker
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

/**
  * Created by Yuval_Aviyam on 15/8/2017.
  */
class ModelManagerTest extends SpecificationWithJUnit with Mockito with ModelManagerMocker {

  "The ModelManager" should {

    "is spam" in pending /*{
      modelManager.isSpam("You have won 100000$ free") must_== 1
      modelManager.isSpam("Hi Yuval, how are you today?") must_== 0
      modelManager.isSpam("Please send me your password") must_== 1
      modelManager.isSpam("send you the package of viagra") must_== 0
    }*/

    "predict gender" in pending /*{
      modelManager.classifyGender("John") must_== Some("m")
      modelManager.classifyGender("Robert") must_== Some("m")
      modelManager.classifyGender("Joan") must_== Some("f")
      modelManager.classifyGender("Alice") must_== Some("f")
    }*/

    "classify page" in pending /*{
      modelManager.classifyPage("Lorem ipsum") must beNone
      //predictManager.classifyPage("contact us") must beNone
      modelManager.classifyPage("Free shipping") must_== Some("return_shipping_policy")
      modelManager.classifyPage("Receive your package") must_== Some("return_shipping_policy")
    }*/

    "stores signals" in pending /*{
      val signals = ModelManager.modelStoreSignalsCorrelation
      signals.dump()
      signals.getFields.size must_== 25

      //val filtered = signals.withFilter(f => f.toArray.drop(12).head > 1000 && f.toArray.drop(14).head > 1000)
      //filtered.dump()
      //filtered.getCount must_== 2952
    }*/

    /*
    "status" in {
      modelManager.status.size must beGreaterThan(0)
    }
    */
  }
}

trait ModelManagerMocker extends BaseExecutorMocker {
  val modelManager = new ModelManager(baseParamsMock)
}