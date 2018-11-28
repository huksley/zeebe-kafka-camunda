package com.github.huksley.zeebe.workers

import io.zeebe.client.ZeebeClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaConsumer {
    companion object {
        val console = LoggerFactory.getLogger(KafkaConsumer::class.java.name)
    }

    @Autowired
    lateinit var zeebe: ZeebeClient

    @KafkaListener(topicPattern = "test", groupId = "\${spring.kafka.consumer.group-id}")
    fun onTestMessage(msgs: List<KafkaMessage>) {
        console.info("Got messages {}", msgs)
    }
}