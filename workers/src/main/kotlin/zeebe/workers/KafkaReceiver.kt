package zeebe.workers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.messaging.annotation.Body
import io.micronaut.messaging.annotation.Header
import io.micronaut.retry.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import java.net.InetAddress
import javax.inject.Inject
import java.util.HashMap



@KafkaListener(threads = 10)
open class KafkaReceiver {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var zeebe: Zeebe

  @Inject
  lateinit var meta: MetaSink

  @CircuitBreaker
  @Topic("test")
  open fun receive(@KafkaKey id: String,
                   @Header("X-Order-ID") orderId: String,
                   @Header("X-Source") source: String,
                   @Header("X-From") from: String,
                   @Body body: Map<String,Any>) {
    log.trace("Received kafka message id = {}, orderId = {}, source = {}, from: {}, body: {} sending to zeebe", id, orderId, source, from, body)
    val payload: Map<String,Any> = mutableMapOf("orderId" to orderId, "source" to source, "from" to from)
    val opts = payload.plus(body)
    meta.receivedKafka ++
    for (i in 1..10) {
      zeebe.createWorkflowInstance("order-process", opts).join()
    }
  }
}
