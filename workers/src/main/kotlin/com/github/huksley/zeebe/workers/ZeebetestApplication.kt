package com.github.huksley.zeebe.workers

import io.zeebe.client.ZeebeClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.ContextStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@SpringBootApplication
@EnableAsync
@EnableScheduling
class ZeebetestApplication {
    companion object {
        val console = LoggerFactory.getLogger(ZeebetestApplication::class.java.name)
    }
}

@Component
class EventListeners {
    companion object {
        val console = LoggerFactory.getLogger(EventListeners::class.java.name)
    }

    @Value("\${zeebe.broker.url}")
    var zeebeBrokerUrl: String = "127.0.0.1:26500"

    var haveWorkflow: Boolean = false

    @Scheduled(fixedDelay = 5000)
    fun runWorkers() {
        console.info("Workers here")
        if (!haveWorkflow) {
            return
        }

        val zb = client()

        var jobWorker = zb.jobClient().
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
            }.open()

        jobWorker.close()
    }

    @Scheduled(fixedDelay = 1000)
    fun createInstances() {
        if (!haveWorkflow) {
            return
        }

        val zb = client()
        var payload = HashMap<String,Any>()
        payload.put("orderId", System.currentTimeMillis())
        payload.put("orderItems", longArrayOf(1, 2, 3, 4))

        val wf = zb.workflowClient()
        var inst = wf.newCreateInstanceCommand().
                bpmnProcessId("order-process").
                latestVersion().
                payload(payload as Map<String,Any>).
                send().
                join()

        console.info("Instance created: {}", inst)
    }

    fun client(): ZeebeClient {
        val zb = ZeebeClient.newClientBuilder().
                brokerContactPoint(zeebeBrokerUrl).
                build()
        return zb
    }

    @EventListener
    fun onApplicationStart(ev: ApplicationReadyEvent) {
        console.info("Got event: {}", ev)

        val zb = client()
        console.info("Got Zeebe client: {}", zb)

        console.info("Sending processes")
        val wf = zb.workflowClient()
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

fun main(args: Array<String>) {
    runApplication<ZeebetestApplication>(*args)
}
