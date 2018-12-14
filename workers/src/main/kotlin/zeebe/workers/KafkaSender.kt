package zeebe.workers

import com.devskiller.jfairy.Fairy
import io.micronaut.retry.annotation.CircuitBreaker
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class KafkaSender {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var sender: KafkaOrderSender

  @Inject
  lateinit var meta: MetaSink

  val fairy = Fairy.create(Locale.GERMANY)

  @CircuitBreaker
  @Scheduled(fixedDelay = "100ms")
  open fun tryResend() {
    val person = fairy.person()
    log.trace("Sending new order to kafka {}", person)
    meta.sendKafka ++
    sender.sendOrder(System.currentTimeMillis().toString(),
      "X" + System.currentTimeMillis(),
      InetAddress.getLocalHost().hostName,
      "kotlin-micronaut-kafka",
      person)

  }
}
