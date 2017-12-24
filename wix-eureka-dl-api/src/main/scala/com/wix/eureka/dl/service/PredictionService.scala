package com.wix.eureka.dl.service

/**
  * Created by Yuval_Aviyam on 5/1/2017.
  */
trait PredictionService {
  def isSpam(text: String): Double
}
