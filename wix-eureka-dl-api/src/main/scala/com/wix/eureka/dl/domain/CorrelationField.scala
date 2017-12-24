package com.wix.eureka.dl.domain

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonInclude}

/**
  * Created by Yuval_Aviyam on 27/8/2017.
  */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class CorrelationField(idx: Int, name: String, correlation: Double, mean: Double, stdev: Double) {

  def withStats(mean: Double, stdev: Double): CorrelationField =
    this.copy(mean = mean, stdev = stdev)

  override def toString: String = s"idx=$idx , name=$name , correlation=$correlation , mean=$mean , stdev=$stdev"
}

object CorrelationField {

  val EMPTY: CorrelationField = CorrelationField(-1, "", 0, 0, 0)

  def apply(idx: Int, correlation: Double, mean: Double, stdev: Double): CorrelationField =
    apply(idx, s"field_$idx", correlation, mean, stdev)
}