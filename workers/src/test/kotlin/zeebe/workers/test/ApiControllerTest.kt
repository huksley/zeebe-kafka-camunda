package zeebe.workers.test

import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import org.slf4j.LoggerFactory
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import zeebe.workers.Application
import kotlin.test.assertEquals

object ApiControllerTest : Spek({
  val log = LoggerFactory.getLogger(javaClass)

  describe("api suite") {
    val embeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
    log.info("Server url ${embeddedServer.url}")
    val httpClient = HttpClient.create(embeddedServer.url)
    val client = httpClient.toBlocking()

    it("test /status responds OK") {
      var rsp: String = client.retrieve("/status")
      log.info("Got response ${rsp}")
      assertEquals(rsp, "OK")
    }

    afterGroup {
      httpClient.close()
      embeddedServer.close()
    }
  }
})
