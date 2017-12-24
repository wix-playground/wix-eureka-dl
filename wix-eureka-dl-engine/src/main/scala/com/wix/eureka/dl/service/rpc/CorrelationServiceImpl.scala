package com.wix.eureka.dl.service.rpc

import com.wix.eureka.dl.ServerComponents
import com.wix.eureka.dl.service.CorrelationService
import org.springframework.beans.factory.annotation.Autowired

/**
  * Created by Yuval_Aviyam on 7/2/2017.
  */
class CorrelationServiceImpl extends CorrelationService {

  @Autowired var serverComponents: ServerComponents = _

  def getCorrelations(column: Int) =
    serverComponents.modelManager.getCorrelations(column)

  def calculateCorrelations(column: Int, row: Seq[Any]) =
    serverComponents.modelManager.calculateCorrelations(column, row)
}
