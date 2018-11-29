package com.github.huksley.zeebe

import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import org.slf4j.LoggerFactory

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
class KafkaReceiver {
  companion object {
    val console = LoggerFactory.getLogger(KafkaReceiver::class.java.name)
  }

  @Topic("test")
  fun receive(@KafkaKey id: String, orderId: String) {
    console.info("Got order: id = {}, orderId = {}", id, orderId)
  }
}
