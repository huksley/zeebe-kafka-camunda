package com.github.huksley.zeebe

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic
import org.slf4j.LoggerFactory

@KafkaClient
interface KafkaOrderSender {

  @Topic("test")
  fun sendOrder(@KafkaKey id: String, orderId: String, from: String, source: String)
}
