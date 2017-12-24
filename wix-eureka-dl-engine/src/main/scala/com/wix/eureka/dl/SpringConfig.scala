package com.wix.eureka.dl

import com.wix.auth.services.BoAuthenticationCookieService
import com.wix.auth.spring.AuthenticationClientSpringBeansConfig
import com.wix.bootstrap.spring.{RpcServerSupport, RpcServiceEndpointDefinition}
import com.wix.eureka.bi.BiConstants
import com.wix.eureka.config.{ConfigurationLoader, ExecutorSupport}
import com.wix.eureka.dl.service._
import com.wix.eureka.dl.service.rpc._
import com.wix.eureka.helpers.BaseExecutorParams
import com.wixpress.framework.async.{WixExecutors, WixFrameworkAsyncBeanConfig}
import com.wixpress.framework.bi.{BILogger, BILoggerFactory}
import com.wixpress.framework.cache.WixCacheBuilders
import com.wixpress.framework.cache.appinfo.spring.CacheAppInfoBeanConfig
import com.wixpress.framework.cache.spring.CacheBuildersBeanConfig
import com.wixpress.framework.context.RequestAspectStore
import com.wixpress.framework.monitoring.metering.MeteringService
import com.wixpress.framework.rpc.discovery.RpcProxyFactory
import com.wixpress.framework.spring.JsonRpcServerConfiguration
import com.wixpress.framework.time.TimeSource
import com.wixpress.petri.laboratory.Laboratory
import org.springframework.context.annotation.{Bean, Configuration, Import}

import scala.concurrent.ExecutionContext

/**
  * Created by Yuval_Aviyam on 1/23/14.
  */
@Configuration
@Import(Array(classOf[WixFrameworkAsyncBeanConfig],
              classOf[JsonRpcServerConfiguration],
              classOf[AuthenticationClientSpringBeansConfig],
              classOf[CacheBuildersBeanConfig],
              classOf[CacheAppInfoBeanConfig]))
class SpringConfig extends RpcServerSupport with ExecutorSupport {

  lazy val config = new ConfigurationLoader("wix-eureka-dl-engine-config.xml")

  @Bean def biLogger: BILogger = BILoggerFactory.aBILoggerFor(BiConstants.SRC)

  @Bean def params(wixExecutors: WixExecutors,
                   executionContext: ExecutionContext,
                   meteringService: MeteringService,
                   requestAspectStore: RequestAspectStore,
                   rpcProxyFactory: RpcProxyFactory,
                   laboratory: Laboratory,
                   timeSource: TimeSource,
                   //domainCookieHeaderLocaleResolver: DomainCookieHeaderLocaleResolver,
                   biLogger: BILogger,
                   wixCacheBuilders: WixCacheBuilders): BaseExecutorParams = {
    BaseExecutorParams(wixExecutors, executionContext, meteringService, requestAspectStore, rpcProxyFactory, laboratory, timeSource, biLogger, wixCacheBuilders, config)
  }

  @Bean def executionContext(wixExecutors: WixExecutors) = getExecutor("eureka-dl")

  @Bean def serverComponents(params: BaseExecutorParams) = new ServerComponents(params, null)

  @Bean def engineController(serverComponents: ServerComponents) = serverComponents.engineController

  /* rpc */

  @Bean def classificationService: ClassificationService = new ClassificationServiceImpl
  @Bean def predictionService: PredictionService = new PredictionServiceImpl
  @Bean def correlationService: CorrelationService = new CorrelationServiceImpl
  @Bean def recommendationService: RecommendationService = new RecommendationServiceImpl

  def rpcServiceEndpoints = Seq(
    RpcServiceEndpointDefinition(classOf[ClassificationService], classificationService),
    RpcServiceEndpointDefinition(classOf[PredictionService], predictionService),
    RpcServiceEndpointDefinition(classOf[CorrelationService], correlationService),
    RpcServiceEndpointDefinition(classOf[RecommendationService], recommendationService)
  )
}