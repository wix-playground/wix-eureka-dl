package com.wix.eureka.dl

import com.wix.bootstrap.BootstrapManagedService
import com.wix.bootstrap.RunServer.Options
import com.wixpress.framework.test.env.TestEnvBuilder

/**
 * Created by Yuval_Aviyam on 2015-10-29.
 */
object EmbeddedEnvironment {
  val SERVER_PORT = 9901
  val RPC_PORT = 9902

  lazy val globalTestEnv =
    TestEnvBuilder()
      .withMainService(BootstrapManagedService(WebServer, Options(port = Some(SERVER_PORT))))
      .withCollaborators(new EmbeddedRpcServer(RPC_PORT))
      .build()
}

