package zeebe.workers

import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.messaging.annotation.Body
import io.micronaut.messaging.annotation.Header
import org.slf4j.LoggerFactory
import java.net.InetAddress
import javax.inject.Inject

@KafkaListener(threads = 10)
open class KafkaReceiver {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var zeebe: Zeebe

  @Topic("test")
  open fun receive(@KafkaKey id: String, @Body orderId: String, @Header("X-Source") source: String, @Header("X-From") from: String) {
    log.info("Received kafka message id = {}, orderId = {}, source = {}, from: {} sending to zeebe", id, orderId, source, from)
    zeebe.createWorkflowInstance("order-process", mapOf("orderId" to orderId, "source" to source, "from" to from)).join()
  }
}
