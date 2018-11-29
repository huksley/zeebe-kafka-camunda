package zeebe.workers.test

import io.micronaut.context.annotation.Primary
import org.slf4j.LoggerFactory
import zeebe.workers.KafkaOrderSender

@Primary
class KafkaNullSender: KafkaOrderSender {
  val log = LoggerFactory.getLogger(javaClass)

  override fun sendOrder(id: String, orderId: String, from: String, source: String) {
    log.info("Not sending order ${id}, ${orderId}")
  }
}
