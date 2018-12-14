package zeebe.workers.order

import io.micronaut.context.annotation.Factory
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async
import io.micronaut.scheduling.annotation.Scheduled
import io.zeebe.client.api.subscription.JobHandler
import org.slf4j.LoggerFactory
import zeebe.workers.KafkaMeta
import zeebe.workers.Zeebe
import javax.inject.Inject
import javax.inject.Singleton

//DONTSTART@Singleton
open class OrderWorkers {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var zeebe: Zeebe

  @Inject
  lateinit var meta: OrderMeta

  //DONTSTART@Scheduled(fixedDelay = "1s")
  fun dumpMeta() {
    val ver = zeebe.getWorkflow("order-process").join().version
    log.info("Workflow v.${ver} processed payment ${meta.paymentProcessed} dt ${meta.paymentProcessed - meta.latestPaymentProcessed}")
    meta.latestPaymentProcessed = meta.paymentProcessed
  }

  @EventListener
  @Async
  open fun onStartup(event: ServerStartupEvent) {
    zeebe.createJobClient("payment-service", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val payload = job.payloadAsMap
        log.trace("Processing payment service {} headers {} payload {}", job.key, headers, payload)
        payload.put("paymentId", System.currentTimeMillis())
        payload.put("paymentStatus", if (System.currentTimeMillis() % 2L == 0L) "SUCCESS" else "FAILURE")
        meta.paymentProcessed ++
        log.trace("Payment complete: ${payload}")
        jobClient.newCompleteCommand(job.key).payload(payload).send().join()
      }
    })

    zeebe.createJobClient("return-money", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val payload = job.payloadAsMap
        meta.returnedPayment ++
        log.trace("Returning money {} headers {} payload {}", job.key, headers, payload)
        payload.put("paymentId", System.currentTimeMillis())
        payload.put("paymentStatus", "RETURN")
        jobClient.newCompleteCommand(job.key).payload(payload).send().join()
      }
    })

    zeebe.createJobClient("inventory-service", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val payload = job.payloadAsMap
        meta.assembledOrders ++
        log.trace("Assembling order {} headers {} payload {}", job.key, headers, payload)
        payload.put("packageId", System.currentTimeMillis())
        payload.put("packageStatus", "READY")
        jobClient.newCompleteCommand(job.key).payload(payload).send().join()
      }
    })

    zeebe.createJobClient("shipment-service", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val payload = job.payloadAsMap
        meta.shippedOrders ++
        log.trace("Shipping order {} headers {} payload {}", job.key, headers, payload)
        payload.put("deliveryId", System.currentTimeMillis())
        payload.put("deliveryStatus", "SENT")
        jobClient.newCompleteCommand(job.key).payload(payload).send().join()
      }
    })
  }
}
