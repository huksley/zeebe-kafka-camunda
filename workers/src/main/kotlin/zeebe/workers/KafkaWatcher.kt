package zeebe.workers

import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class KafkaWatcher {
  @Inject
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var meta: KafkaMeta

  @Scheduled(fixedDelay = "1s")
  fun dumpMeta() {
    // log.info("Kafka stat sent: ${meta.sent} received: ${meta.received}")
  }

}
