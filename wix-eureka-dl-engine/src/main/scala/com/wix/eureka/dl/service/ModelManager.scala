package com.wix.eureka.dl.service

import com.wix.eureka.dl.domain.{CorrelationField, CorrelationScore}
import com.wix.eureka.dl.engines.spark.{ClassificationModel, CorrelationModel}
import com.wix.eureka.helpers.{BaseExecutorParams, BaseManager}

/**
  * Created by Yuval_Aviyam on 24/8/2016.
  */
class ModelManager(params: BaseExecutorParams)

  extends BaseManager(params) {

  import ModelManager._

  /*
  def status: Seq[String] = Seq(
    modelTextSpamDetection.status,
    modelNameGenderDetection.status,
    modelShippingPolicyDetection.status
    //modelStoreSignalsCorrelation.status
  )
  */

  def isSpam(text: String): Double =
    modelTextSpamDetection.predict(text)

  def classifyGender(name: String): Option[String] =
    modelNameGenderDetection.classify(name)

  def classifyPage(name: String): Option[String] =
    modelShippingPolicyDetection.classify(name)

  def getCorrelations(column: Int): List[CorrelationField] =
    modelStoreSignalsCorrelation.getCorrelations(column)

  def calculateCorrelations(column: Int, row: Seq[Any]): List[CorrelationScore] =
    modelStoreSignalsCorrelation.calculateCorrelations(column, row)
}

object ModelManager {
  lazy val modelTextSpamDetection = ClassificationModel("spam")
  lazy val modelNameGenderDetection = ClassificationModel("gender", Map("m" -> 1.0, "f" -> 0.0), threshold = 1.0)
  lazy val modelShippingPolicyDetection = ClassificationModel("policies", Map("return_shipping_policy" -> 1.0), threshold = 1.0)

  lazy val modelStoreSignalsCorrelation = CorrelationModel("signals", "stores")
}