package zeebe.workers

import io.micronaut.context.annotation.Factory
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async
import io.micronaut.scheduling.annotation.Scheduled
import io.zeebe.client.api.subscription.JobHandler
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Factory
open class ZeebeProcessor {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var zeebe: Zeebe

  @Scheduled(fixedDelay = "1s")
  fun createInstances() {
    var payload = HashMap<String, Any>()
    payload.put("orderId", System.currentTimeMillis())
    payload.put("orderItems", longArrayOf(1, 2, 3, 4))
    zeebe.createWorkflowInstance("order-process", payload).join()
  }

  @EventListener
  @Async
  open fun onStartup(event: ServerStartupEvent) {
    zeebe.deployProcessWorkflow("order-process", "../test-data/order-process.bpmn").join()

    zeebe.createJobClient("payment-service", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val payload = job.payloadAsMap
        log.info("Processing payment service {} headers {} payload {}", job.key, headers, payload)
        payload.put("paymentId", System.currentTimeMillis())
        jobClient.newCompleteCommand(job.key).payload(payload).send().join()
      }
    })
  }
}
