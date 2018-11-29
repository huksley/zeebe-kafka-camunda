package zeebe.workers

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer

//@Singleton
class KafkaSetup {
  val BOOTSTRAP_SERVERS = "localhost:9092"
  val GROUP_ID = "test1"
  var CLIENT_ID = "test1-client1"

  val consumer: Consumer<String,String> by lazy {
    createConsumer()
  }

  val producer: Producer<String,String> by lazy {
    createProducer()
  }

  private fun createConsumer(): Consumer<String, String> {
    val props = HashMap<String,String>()
    props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
    props[ConsumerConfig.GROUP_ID_CONFIG] = GROUP_ID
    props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
    props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
    return KafkaConsumer(props as Map<String, Any>?)
  }

  private fun createProducer(): Producer<String, String> {
    val props = HashMap<String,String>()
    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
    props[ProducerConfig.CLIENT_ID_CONFIG] = CLIENT_ID
    props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
    return KafkaProducer(props as Map<String, Any>?)
  }
}
