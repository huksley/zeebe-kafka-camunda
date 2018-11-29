package zeebe.workers

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.zeebe.client.ZeebeClient

@Factory
class ZeebeSetup {
  var zeebeBrokerUrl: String = "127.0.0.1:26500"

  @Bean
  fun zeebeClient(): ZeebeClient {
    return ZeebeClient.newClientBuilder().brokerContactPoint(zeebeBrokerUrl).build()
  }
}
