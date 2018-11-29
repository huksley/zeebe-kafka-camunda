package com.github.huksley.zeebe

import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async
import org.slf4j.LoggerFactory
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KafkaSender {
  companion object {
    val console = LoggerFactory.getLogger(KafkaReceiver::class.java.name)
  }

  @Inject
  lateinit var sender: KafkaOrderSender

  @EventListener
  @Async
  private fun onStartup(event: ServerStartupEvent) {
    console.info("Startup {}", event)
    sender.sendOrder(System.currentTimeMillis().toString(), "dasdsds", InetAddress.getLocalHost().hostName, "kotlin-micronaut-worker")
  }
}
