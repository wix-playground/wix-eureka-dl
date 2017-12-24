package com.wix.eureka.dl.service.rpc

import com.wix.eureka.dl.ServerComponents
import com.wix.eureka.dl.service.PredictionService
import org.springframework.beans.factory.annotation.Autowired

/**
  * Created by Yuval_Aviyam on 7/2/2017.
  */
class PredictionServiceImpl extends PredictionService {

  @Autowired var serverComponents: ServerComponents = _

  def isSpam(text: String) =
    serverComponents.modelManager.isSpam(text)
}