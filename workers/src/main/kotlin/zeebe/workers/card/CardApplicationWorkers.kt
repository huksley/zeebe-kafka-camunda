package zeebe.workers.card

import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async
import io.micronaut.scheduling.annotation.Scheduled
import io.zeebe.client.api.subscription.JobHandler
import org.slf4j.LoggerFactory
import zeebe.workers.Zeebe
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CardApplicationWorkers {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var zeebe: Zeebe

  @Inject
  lateinit var meta: CardMeta

  @Scheduled(fixedDelay = "1s")
  fun dumpMeta() {
    val ver = zeebe.getWorkflow("open-card").join().version
    log.info("Workflow v.${ver} kyc: ${meta.kycDone} score: ${meta.scoreDone} msg: ${meta.msgDone} createContract: ${meta.createContractDone} scoreRate: ${meta.scoreRateDone}")
  }

  @EventListener
  @Async
  open fun onStartup(event: ServerStartupEvent) {
    zeebe.createJobClient("kyc", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val inPayload = job.payloadAsMap
        log.trace("KYC service {} headers {} payload {}", job.key, headers, inPayload)
        val outPayload = mapOf(
          "kycReference" to UUID.randomUUID().toString(),
          "kycStatus" to if (System.currentTimeMillis() % 4L != 0L) "SUCCESS" else "FAILURE"
        )
        meta.kycDone ++
        log.trace("KYC complete: {}", outPayload)
        jobClient.newCompleteCommand(job.key).payload(outPayload).send().join()
      }
    })

    zeebe.createJobClient("score", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val inPayload = job.payloadAsMap
        log.trace("Score service {} headers {} payload {}", job.key, headers, inPayload)
        val outPayload = mapOf(
          "scoreReference" to UUID.randomUUID().toString(),
          "scoreStatus" to if (System.currentTimeMillis() % 4L != 0L) "SUCCESS" else "FAILURE",
          "score" to BigDecimal(500)
        )
        meta.scoreDone ++
        log.trace("Score complete: {}", outPayload)
        jobClient.newCompleteCommand(job.key).payload(outPayload).send().join()
      }
    })

    zeebe.createJobClient("msg", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val inPayload = job.payloadAsMap
        log.trace("Msg service {} headers {} payload {}", job.key, headers, inPayload)
        val outPayload = mapOf(
          "no" to "payload"
        )
        meta.msgDone ++
        log.trace("Msg complete: {}", outPayload)
        jobClient.newCompleteCommand(job.key).payload(outPayload).send().join()
      }
    })

    zeebe.createJobClient("create-contract", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val inPayload = job.payloadAsMap
        log.trace("Create contract service {} headers {} payload {}", job.key, headers, inPayload)
        val outPayload = mapOf(
          "contract" to mapOf(
            "contractNo" to "1"
          ),
          "contractReference" to UUID.randomUUID().toString()
        )
        meta.msgDone ++
        log.trace("Create contract complete: {}", outPayload)
        jobClient.newCompleteCommand(job.key).payload(outPayload).send().join()
      }
    })

    zeebe.createJobClient("score-rate", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val inPayload = job.payloadAsMap
        log.trace("Score determine rate service {} headers {} payload {}", job.key, headers, inPayload)
        val contract: MutableMap<String,Any> = inPayload["contract"] as MutableMap<String, Any>
        contract["rate"] = BigDecimal("12.25")
        val outPayload = mapOf(
          "contract" to inPayload["contract"]
        )
        meta.msgDone ++
        log.trace("Score determine rate complete: {}", outPayload)
        jobClient.newCompleteCommand(job.key).payload(outPayload).send().join()
      }
    })
  }
}
