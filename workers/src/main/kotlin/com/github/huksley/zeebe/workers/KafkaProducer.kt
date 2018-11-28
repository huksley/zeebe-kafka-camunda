package com.github.huksley.zeebe.workers

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.InetAddress

@Component
class KafkaProducer {
    companion object {
        val console = LoggerFactory.getLogger(KafkaProducer::class.java.name)
    }

    @Autowired
    lateinit var kafka: KafkaTemplate<String,String>

    @Scheduled(fixedRate = 1000)
    @Async
    fun produceMessage() {

        val promise = kafka.send("test", "testmsg"+ System.currentTimeMillis(), "{ \"from\": \"kafka\", \"source\": \"${InetAddress.getLocalHost().hostName}\", \"orderId\": ${System.currentTimeMillis()} }")
        promise.addCallback({
            result -> console.info("Success $result")
        }, {
            exception -> console.info("Failed to send msg: $exception")
        })
    }
}