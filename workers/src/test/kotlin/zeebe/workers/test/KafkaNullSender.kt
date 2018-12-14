package zeebe.workers.test

import com.devskiller.jfairy.producer.person.Person
import io.micronaut.context.annotation.Primary
import org.slf4j.LoggerFactory
import zeebe.workers.KafkaSender
import zeebe.workers.order.Order

@Primary
class KafkaNullSender: KafkaSender {
  val log = LoggerFactory.getLogger(javaClass)

  override fun sendCardApplication(id: String, applicationId: String, from: String, source: String, person: Person) {
    log.info("Not sending order ${id}, ${applicationId}, ${person}")
  }

  override fun sendOrder(id: String, orderId: String, from: String, source: String, order: Order) {
    log.info("Not sending order ${id}, ${orderId}, ${order}")
  }
}
