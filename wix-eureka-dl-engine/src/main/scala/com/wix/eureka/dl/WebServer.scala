package com.wix.eureka.dl

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.wix.bootstrap.jetty.JettyBasedServer.JettyPoolConfig
import com.wix.bootstrap.jetty._
import com.wix.bootstrap.spring.CustomJacksonConfig
import com.wixpress.datastore.json.HtmlJacksonModule

/**
 * Created by Yuval_Aviyam on 1/23/14.
 */
object WebServer extends BootstrapServer with CustomThreadPool with CustomJacksonConfig {
  override def additionalSpringConfig = Some(classOf[SpringConfig])

  override val threadPoolConfig = JettyPoolConfig(minThreads = 20, maxThreads = 100)

  override def configureObjectMapper(objectMapper: ObjectMapper): Unit = {
    objectMapper.setSerializationInclusion(Include.NON_NULL)
    objectMapper.registerModule(new HtmlJacksonModule)
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
    objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
  }
}