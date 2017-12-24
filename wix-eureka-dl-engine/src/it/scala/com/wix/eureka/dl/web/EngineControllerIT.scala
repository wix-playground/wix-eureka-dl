package com.wix.eureka.dl.web

import com.wix.e2e.SessionSupport
import com.wix.e2e.http.client.sync._
import com.wix.eureka.dl.E2ETest

/**
 * Created by Yuval_Aviyam on 2/17/14.
 */
class EngineControllerIT extends E2ETest with SessionSupport {

  "The EngineController" should {

    val root = ""

    "hello" in {
      get(path = s"$root/hello") must haveStatus(200) and haveBodyThat(contain("world"))
    }
  }
}
