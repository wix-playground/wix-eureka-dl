package com.wix.eureka.dl.web

import com.wix.eureka.helpers.{BaseController, BaseExecutorParams}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{RequestMapping, ResponseBody}

/**
 * Created by Yuval_Aviyam on 2/11/14.
 */
@Controller
@RequestMapping(value = Array(""))
class EngineController(params: BaseExecutorParams)

  extends BaseController(params) {

  @RequestMapping(value = Array("hello"), produces = Array(HTML))
  @ResponseBody
  def hello() = {
    "world"
  }
}