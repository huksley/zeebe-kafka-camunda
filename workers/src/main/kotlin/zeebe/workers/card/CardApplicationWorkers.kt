package zeebe.workers.card

import io.micronaut.context.event.ApplicationEventPublisher
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
@Suppress("unused")
open class CardApplicationWorkers {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var zeebe: Zeebe

  @Inject
  lateinit var meta: CardMeta

  @Inject
  lateinit var eventPublisher: ApplicationEventPublisher

  val random = Random()

  class SendMessage (val messageId: String, val correlationId: String)

  @Scheduled(fixedDelay = "1s")
  @Suppress("unused")
  fun dumpMeta() {
    val ver = zeebe.getWorkflow("open-card").join().version
    log.info("Open card stat version ${ver} kyc: ${meta.kycDone} score: ${meta.scoreDone} msg: ${meta.msgDone} createContract: ${meta.createContractDone} scoreRate: ${meta.scoreRateDone} contractConfirm: ${meta.contractConfirmDone} issueCard: ${meta.issueCardDone}")
  }

  @EventListener
  @Async
  @Suppress("unused")
  open fun onSendMessage(event: SendMessage) {
    log.trace("Sending message to Zeebe: ${event.messageId}, ${event.correlationId}")
    meta.contractConfirmDone ++
    zeebe.sendMessage(event.messageId, event.correlationId)
  }

  @EventListener
  @Async
  @Suppress("unused")
  open fun onStartup(event: ServerStartupEvent) {
    zeebe.createJobClient("kyc", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val inPayload = job.payloadAsMap
        log.trace("KYC service {} headers {} payload {}", job.key, headers, inPayload)
        val outPayload = mapOf(
          "kycReference" to UUID.randomUUID().toString(),
          "kycStatus" to if (random.nextInt(4) != 3) "SUCCESS" else "FAILURE" // 1/4 failure
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
        val score = random.nextInt(700) + 300
        val outPayload = mapOf(
          "scoreReference" to UUID.randomUUID().toString(),
          "scoreStatus" to if (score < 400) "BELOW_MIN" else (if (score > 700) "WHITE" else "GRAY"),
          "score" to BigDecimal(score)
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
        meta.createContractDone ++
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
        meta.scoreRateDone ++
        log.trace("Score determine rate complete: {}", outPayload)
        jobClient.newCompleteCommand(job.key).payload(outPayload).send().join()
        // Fake async message external
        eventPublisher.publishEvent(SendMessage("contract-confirm", inPayload["applicationId"] as String))
      }
    })

    zeebe.createJobClient("issue-card", JobHandler { jobClient, job ->
      run {
        val headers = job.customHeaders
        val inPayload = job.payloadAsMap
        log.trace("Issue card service {} headers {} payload {}", job.key, headers, inPayload)
        val contract: MutableMap<String,Any> = inPayload["contract"] as MutableMap<String, Any>
        contract["rate"] = BigDecimal("12.25")
        val outPayload = mapOf(
          "no" to "none"
        )
        meta.issueCardDone ++
        log.trace("Issue card complete: {}", outPayload)
        jobClient.newCompleteCommand(job.key).payload(outPayload).send().join()
      }
    })
  }
}
