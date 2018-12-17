package zeebe.workers.card

import com.devskiller.jfairy.Fairy
import io.micronaut.retry.annotation.CircuitBreaker
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import zeebe.workers.KafkaMeta
import zeebe.workers.KafkaSender
import java.net.InetAddress
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sends card applications to kafka, should be external component but for example it is implemented here
 */
@Singleton
open class FakeCardApplicationProducer {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var sender: KafkaSender

  @Inject
  lateinit var meta: KafkaMeta

  /**
   * Random customer detail factoryd
   */
  private val fairy: Fairy = Fairy.create(Locale.GERMANY)

  @CircuitBreaker
  @Scheduled(fixedDelay = "10ms")
  open fun trySendCardApplication() {
    val person = fairy.person()
    log.trace("Sending new card application to kafka {}", person)
    meta.sent.incrementAndGet()
    sender.sendCardApplication(System.currentTimeMillis().toString(),
      UUID.randomUUID().toString(),
      InetAddress.getLocalHost().hostName,
      "kotlin-micronaut-kafka",
      person)
  }
}
