package zeebe.workers.test

import com.devskiller.jfairy.producer.person.Person
import io.micronaut.context.annotation.Primary
import org.slf4j.LoggerFactory
import zeebe.workers.KafkaOrderSender

@Primary
class KafkaNullSender: KafkaOrderSender {
  val log = LoggerFactory.getLogger(javaClass)

  override fun sendOrder(id: String, orderId: String, from: String, source: String, person: Person) {
    log.info("Not sending order ${id}, ${orderId}, ${person}")
  }
}
