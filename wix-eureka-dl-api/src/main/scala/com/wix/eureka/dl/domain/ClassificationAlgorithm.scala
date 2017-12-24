package com.wix.eureka.dl.domain

import com.fasterxml.jackson.annotation.JsonIgnore

/**
  * Created by Yuval_Aviyam on 10/7/2017.
  */
object ClassificationAlgorithm extends Enumeration {
  type ClassificationAlgorithm = Value

  @JsonIgnore
  val LogisticRegression, DecisionTree, SVM, NaiveBayes, RandomForest = Value
}
