package com.github.huksley.zeebe.workers

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.*
import org.springframework.stereotype.Component
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties



@Component
@Configuration
class KafkaConfigurer {
    companion object {
        val console = LoggerFactory.getLogger(KafkaConfigurer::class.java.name)
    }

    @Value("\${kafka.host:localhost}")
    private val host: String? = null

    @Value("\${kafka.port:9092}")
    private val port: Int = 0

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val configProps = HashMap<String, String>()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        configProps[ProducerConfig.ACKS_CONFIG] = "1"
        configProps[ProducerConfig.RETRIES_CONFIG] = "0"
        configProps[ProducerConfig.BATCH_SIZE_CONFIG] = "10"
        configProps[ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG] = "20000"
        return DefaultKafkaProducerFactory(configProps as Map<String, Any>)
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<Any, Any> {
        val configProps = HashMap<String, String>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configProps[ConsumerConfig.CLIENT_ID_CONFIG] = "323232"
        return DefaultKafkaConsumerFactory(configProps as Map<String, Any>)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    /*
    @Autowired
    lateinit var output: Source


    @Scheduled(fixedDelay = 1000)
    fun produceMessage() {
        val JSON = jacksonObjectMapper()
        var payload = hashMapOf("orderId" to "1") as Map<String,Any>
        val dataString = JSON.writeValueAsString(payload)
        val map =  hashMapOf(org.springframework.messaging.MessageHeaders.CONTENT_TYPE to "application/octet-stream") as Map< String, Any>
        val dataBytes = JSON.writeValueAsBytes(dataString)
        val msg = MessageBuilder.createMessage(dataBytes, MessageHeaders(map))
        console.info("Sending message: {}", msg);
        output.output().send(msg)
    }
    */
}