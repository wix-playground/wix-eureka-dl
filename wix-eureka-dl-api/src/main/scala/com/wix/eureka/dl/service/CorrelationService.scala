package com.wix.eureka.dl.service

import com.wix.eureka.dl.domain.{CorrelationField, CorrelationScore}

/**
  * Created by Yuval_Aviyam on 5/1/2017.
  */
trait CorrelationService {
  def getCorrelations(column: Int): List[CorrelationField]
  def calculateCorrelations(column: Int, row: Seq[Any]): List[CorrelationScore]
}
