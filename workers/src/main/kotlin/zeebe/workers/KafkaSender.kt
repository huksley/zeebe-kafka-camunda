package zeebe.workers

import com.devskiller.jfairy.producer.person.Person
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.messaging.annotation.Body
import io.micronaut.messaging.annotation.Header
import org.slf4j.LoggerFactory
import zeebe.workers.order.Order

/**
 * Single interface to send messages to Kafka
 */
@KafkaClient
interface KafkaSender {

  @Topic("order")
  fun sendOrder(@KafkaKey id: String,
                @Header("X-Order-ID") orderId: String,
                @Header("X-From") from: String,
                @Header("X-Source") source: String, @Body order: Order)

  @Topic("card-application")
  fun sendCardApplication(@KafkaKey id: String,
                @Header("X-Application-ID") applicationId: String,
                @Header("X-From") from: String,
                @Header("X-Source") source: String, @Body person: Person)
}
