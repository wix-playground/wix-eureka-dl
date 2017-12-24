package com.wix.eureka.dl.domain

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonInclude}
import com.wix.one.common.helpers.NumberUtils

/**
  * Created by Yuval_Aviyam on 23/8/2017.
  */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
case class CorrelationScore(field: CorrelationField, value: Double) {

  override def toString: String = s"$field , value=$value , score=$score"

  def score: Double = CorrelationScore.calculateScore(this)

  @JsonIgnore
  def scoreValueEqual: Double = score

  @JsonIgnore
  def scoreValueLower: Double = if (value < field.mean) score else 1.0

  @JsonIgnore
  def scoreValueGreater: Double = if (value > field.mean) score else 1.0
}

object CorrelationScore extends NumberUtils {

  import math._

  val EMPTY: CorrelationScore = CorrelationScore(CorrelationField.EMPTY, 0)

  def apply(value: Double, correlation: Double, mean: Double, stdev: Double): CorrelationScore =
    apply(CorrelationField(0, correlation, mean, stdev), value)

  /*
  def calculateScore(value: Double, mean: Double, stdev: Double): Double =
    (1.0 - 0.34 * (abs(value - mean) / stdev)).rounded(3)
  */

  def calculateScore(value: Double, mean: Double, stdev: Double): Double =
    pow((1.0 / (stdev * sqrt(2.0 * Pi))) * E, pow(value - mean, 2) / (2.0 * pow(stdev, 2))).rounded(3)

  def calculateScore(score: CorrelationScore): Double = if (score.field.correlation == 0)
    0.0
  else if (score.value == score.field.mean)
    abs(score.field.correlation).rounded(3)
  else if (score.field.stdev > 0)
    (calculateScore(score.value, score.field.mean, score.field.stdev) * abs(score.field.correlation)).rounded(3)
  else
    0.0
}