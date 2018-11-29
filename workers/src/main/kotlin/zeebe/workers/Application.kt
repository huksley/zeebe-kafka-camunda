package zeebe.workers

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.Micronaut
import io.zeebe.client.ZeebeClient
import org.slf4j.LoggerFactory


object Application {

  @JvmStatic
  fun main(args: Array<String>) {
    Micronaut.build()
      .packages(Application.javaClass.`package`.name)
      .mainClass(Application.javaClass)
      .start()
  }
}
