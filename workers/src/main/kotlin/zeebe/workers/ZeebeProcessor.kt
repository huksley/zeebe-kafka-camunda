package zeebe.workers

import io.micronaut.context.annotation.Factory
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async
import io.micronaut.scheduling.annotation.Scheduled
import io.zeebe.client.api.subscription.JobHandler
import org.slf4j.LoggerFactory
import java.net.Inet4Address
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Factory
open class ZeebeProcessor {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var zeebe: Zeebe

  @Inject
  lateinit var meta: MetaSink

  @Scheduled(fixedDelay = "")
  fun createInstances() {
    var payload = HashMap<String, Any>()
    payload.put("orderId", System.currentTimeMillis())
    payload.put("from", InetAddress.getLocalHost().hostName)
    payload.put("source", "ZeebeProcessor")
    //log.info("Creating order ${payload}")
    //zeebe.createWorkflowInstance("order-process", payload).join()
  }

  var lastValue = 0;

  @Scheduled(fixedDelay = "1s")
  fun dumpMeta() {
    log.info("Processed payment ${meta.paymentProcessed} dt ${meta.paymentProcessed - lastValue}, send to kafka ${meta.sendKafka} received from kafka ${meta.receivedKafka}")
    lastValue = meta.paymentProcessed
  }

  @EventListener
  @Async
  open fun onStartup(event: ServerStartupEvent) {
    val ev = zeebe.deployProcessWorkflow("order-process", "../test-data/order-process.bpmn").join()
    log.info("Got deployment response: ${ev}")

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
