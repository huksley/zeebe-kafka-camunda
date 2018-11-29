package zeebe.workers

import io.micronaut.retry.annotation.CircuitBreaker
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class KafkaSender {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var sender: KafkaOrderSender

  @CircuitBreaker
  @Scheduled(fixedDelay = "250ms")
  open fun tryResend() {
    log.info("Sending new order to kafka")
    sender.sendOrder(System.currentTimeMillis().toString(),
      "X" + System.currentTimeMillis(),
      InetAddress.getLocalHost().hostName, "kotlin-micronaut-kafka")
  }
}
