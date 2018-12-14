package zeebe.workers.order

import com.devskiller.jfairy.Fairy
import io.micronaut.retry.annotation.CircuitBreaker
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import zeebe.workers.KafkaMeta
import zeebe.workers.KafkaSender
import java.math.BigDecimal
import java.net.InetAddress
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sends orders to kafka, should be external component but for example it is implemented here
 */
//DONTSTART@Singleton
open class FakeOrderProducer {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var sender: KafkaSender

  @Inject
  lateinit var meta: KafkaMeta

  private val random = Random()

  @CircuitBreaker
  @Scheduled(fixedDelay = "100ms")
  open fun trySendOrder() {
    val order = Order(BigDecimal(random.nextInt(9000) + 1000))
    log.trace("Sending new order to kafka {}", order)
    meta.sent.incrementAndGet()
    sender.sendOrder(System.currentTimeMillis().toString(),
      "X" + System.currentTimeMillis(),
      InetAddress.getLocalHost().hostName,
      "kotlin-micronaut-kafka",
      order)
  }
}
