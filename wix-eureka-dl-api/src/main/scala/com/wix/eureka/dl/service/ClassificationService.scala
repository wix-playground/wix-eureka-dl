package com.wix.eureka.dl.service

/**
  * Created by Yuval_Aviyam on 5/1/2017.
  */
trait ClassificationService {
  def classifyPage(text: String): Option[String]
}
