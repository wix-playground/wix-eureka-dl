package com.wix.eureka.dl.domain

import com.fasterxml.jackson.annotation.JsonIgnore

/**
  * Created by Yuval_Aviyam on 10/7/2017.
  */
object CorrelationAlgorithm extends Enumeration {
  type CorrelationAlgorithm = Value

  @JsonIgnore
  val pearson, spearman = Value
}
