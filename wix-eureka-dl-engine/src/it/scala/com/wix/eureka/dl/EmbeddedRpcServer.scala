package com.wix.eureka.dl

import com.wixpress.framework.test.embeddedRpc._
import com.wixpress.petri.petri.{FakePetriServer, RAMPetriClient}
import com.wixpress.petri.util.PetriTestUtils
import org.specs2.mock.Mockito

/**
 * Created by Yuval_Aviyam on 2/25/14.
 */
class EmbeddedRpcServer(port: Int) extends ITEmbeddedRpcServer with Mockito {

  val fakePetri = new RAMPetriClient
  FakePetriServer.reuse(this, fakePetri)
  val petriTestUtils = new PetriTestUtils(fakePetri)

  //this.endpoints += Endpoint[MetaSiteManager](null)

  this.setPort(port)

  sys.addShutdownHook {
    super.stop()
  }
}