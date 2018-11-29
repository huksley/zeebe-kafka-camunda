package zeebe.workers

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.messaging.annotation.Header
import org.slf4j.LoggerFactory

@KafkaClient
interface KafkaOrderSender {

  @Topic("test")
  fun sendOrder(@KafkaKey id: String, orderId: String, @Header("X-From") from: String, @Header("X-Source") source: String)
}
