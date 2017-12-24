package com.wix.eureka.dl

import com.wix.e2e.http.BaseUri
import com.wix.e2e.http.matchers.ResponseMatchers
import com.wixpress.framework.test.env.GlobalTestEnvSupport
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.SpecificationWithJUnit

import scala.concurrent.duration._

/**
 * Created by Yuval_Aviyam on 3/13/14.
 */
trait E2ETest extends SpecificationWithJUnit with GlobalTestEnvSupport with ResponseMatchers with JsonMatchers {
  implicit val port = new BaseUri(port = EmbeddedEnvironment.SERVER_PORT)

  override def testEnv = EmbeddedEnvironment.globalTestEnv

  val timeoutDuration = 10.seconds

  //def getResponse(implicit response: Future[HttpResponse]): String = Await.result(response, timeoutDuration).entity.asString
}