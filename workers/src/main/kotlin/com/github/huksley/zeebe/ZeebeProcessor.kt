package com.github.huksley.zeebe

import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async
import io.micronaut.scheduling.annotation.Scheduled
import io.zeebe.client.ZeebeClient
import org.slf4j.LoggerFactory
import java.time.Duration
import javax.inject.Singleton

@Singleton
class ZeebeProcessor {
    companion object {
        val console = LoggerFactory.getLogger(ZeebeProcessor::class.java.name)
    }

    var zeebeBrokerUrl: String = "127.0.0.1:26500"
    var haveWorkflow = false
    val zeebe: ZeebeClient by lazy {
      ZeebeClient.newClientBuilder().
        brokerContactPoint(zeebeBrokerUrl).
        build()
   }

    @Scheduled(fixedDelay = "5s")
    fun runWorkers() {
        console.info("Workers here {}", haveWorkflow)
        if (!haveWorkflow) {
            return
        }

        var jobWorker = zeebe.jobClient().
            newWorker().
            jobType("payment-service").
            handler { jc, job -> run {
                    val headers = job.customHeaders
                    val payload = job.payloadAsMap
                    console.info("Processing payment service {} headers {} payload {}", job.key, headers, payload)

                    payload.put("paymentId", System.currentTimeMillis())
                    jc.newCompleteCommand(job.key).
                        payload(payload).
                        send().
                        join()
                }
            }.
            timeout(Duration.ofMillis(1000)).
            bufferSize(10).
            pollInterval(Duration.ofMillis(1000)).
            open()
    }

    @Scheduled(fixedDelay = "1s")
    fun createInstances() {
        if (!haveWorkflow) {
            return
        }

        var payload = HashMap<String,Any>()
        payload.put("orderId", System.currentTimeMillis())
        payload.put("orderItems", longArrayOf(1, 2, 3, 4))

        val wf = zeebe.workflowClient()
        var inst = wf.newCreateInstanceCommand().
                bpmnProcessId("order-process").
                latestVersion().
                payload(payload as Map<String,Any>).
                send().
                join()

        console.info("Instance created: {}", inst)
    }

    @EventListener
    @Async
    private fun onStartup(event: ServerStartupEvent) {
        console.info("Got event: {}", event)

        console.info("Got Zeebe client: {}", zeebe)

        console.info("Sending processes")
        val wf = zeebe.workflowClient()
        var dep = wf.newDeployCommand().
            addResourceFile("../test-data/order-process.bpmn").
            send().
            join()

        for (n in dep.getWorkflows()) {
            console.info("Deployed workflow: {}", n.version)
        }

        haveWorkflow = true
    }


}
