package zeebe.workers.order

import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.messaging.annotation.Body
import io.micronaut.messaging.annotation.Header
import io.micronaut.retry.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import zeebe.workers.KafkaMeta
import zeebe.workers.Zeebe
import javax.inject.Inject

/**
 * Receives orders from Kafka and creates workflow instances
 */
//DONTSTART@KafkaListener(threads = 10)
open class KafkaOrderReceiver {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var zeebe: Zeebe

  @Inject
  lateinit var kafkaMeta: KafkaMeta

  @CircuitBreaker
  @Topic("order")
  open fun receive(@KafkaKey id: String,
                   @Header("X-Order-ID") orderId: String,
                   @Header("X-Source") source: String,
                   @Header("X-From") from: String,
                   @Body body: Map<String, Any>) {
    log.trace("Received kafka message id = {}, orderId = {}, source = {}, from: {}, body: {} sending to zeebe", id, orderId, source, from, body)
    val payload: Map<String, Any> = mutableMapOf(
      "orderId" to orderId,
      "source" to source,
      "from" to from)
    val opts = payload.plus(body)
    kafkaMeta.received.incrementAndGet()
    for (i in 1..1) {
      zeebe.createWorkflowInstance("order-process", opts).join()
    }
  }
}
