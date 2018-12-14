package zeebe.workers.card

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
@KafkaListener(threads = 10)
open class KafkaCardApplicationReceiver {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var zeebe: Zeebe

  @Inject
  lateinit var kafkaMeta: KafkaMeta

  @CircuitBreaker
  @Topic("card-application")
  open fun receive(@KafkaKey id: String,
                   @Header("X-Application-ID") applicationId: String,
                   @Header("X-Source") source: String,
                   @Header("X-From") from: String,
                   @Body body: Map<String, Any>) {
    log.trace("Received kafka message id = {}, applicationId = {}, source = {}, from: {}, body: {} sending to zeebe", id, applicationId, source, from, body)
    val payload: Map<String, Any> = mutableMapOf(
      "applicationId" to applicationId,
      "source" to source,
      "from" to from,
      "customer" to body
    )
    kafkaMeta.received.incrementAndGet()
    for (i in 1..1) {
      zeebe.createWorkflowInstance("open-card", payload).join()
    }
  }
}
