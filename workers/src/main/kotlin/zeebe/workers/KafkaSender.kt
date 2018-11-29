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
  companion object {
    val console = LoggerFactory.getLogger(KafkaSender::class.java.name)
  }

  @Inject
  lateinit var sender: KafkaOrderSender

  @EventListener
  @Async
  open fun onStartup(event: ServerStartupEvent) {
    console.info("Startup {}", event)
    sender.sendOrder(System.currentTimeMillis().toString(), "dasdsds", InetAddress.getLocalHost().hostName, "kotlin-micronaut-worker")
  }

  @CircuitBreaker
  @Scheduled(fixedDelay = "250ms")
  open fun tryResend() {
    sender.sendOrder(System.currentTimeMillis().toString(), "dasdsds" + System.currentTimeMillis(), InetAddress.getLocalHost().hostName, "kotlin-micronaut-worker")
  }

}
