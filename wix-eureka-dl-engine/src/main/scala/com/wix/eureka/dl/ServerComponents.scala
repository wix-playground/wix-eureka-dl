package com.wix.eureka.dl

import com.wix.auth.services.BoAuthenticationCookieService
import com.wix.eureka.dl.service.ModelManager
import com.wix.eureka.dl.web.EngineController
import com.wix.eureka.helpers.BaseExecutorParams

/**
  * Created by Yuval_Aviyam on 18/7/2016.
  */
class ServerComponents(val params: BaseExecutorParams,
                       val boAuthCookieService: BoAuthenticationCookieService) {

  lazy val modelManager = new ModelManager(params)

  lazy val engineController = new EngineController(params)

  //println(modelManager.status.mkString("\r\n"))
}
