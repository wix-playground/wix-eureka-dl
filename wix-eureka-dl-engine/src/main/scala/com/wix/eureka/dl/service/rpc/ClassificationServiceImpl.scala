package com.wix.eureka.dl.service.rpc

import com.wix.eureka.dl.ServerComponents
import com.wix.eureka.dl.service.ClassificationService
import org.springframework.beans.factory.annotation.Autowired

/**
  * Created by Yuval_Aviyam on 7/2/2017.
  */
class ClassificationServiceImpl extends ClassificationService {

  @Autowired var serverComponents: ServerComponents = _

  def classifyPage(text: String) =
    serverComponents.modelManager.classifyPage(text)
}
