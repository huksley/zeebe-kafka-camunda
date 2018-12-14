package zeebe.workers

import io.zeebe.client.ZeebeClient
import io.zeebe.client.api.ZeebeFuture
import io.zeebe.client.api.commands.DeployWorkflowCommandStep1
import io.zeebe.client.api.commands.WorkflowResource
import io.zeebe.client.api.events.DeploymentEvent
import io.zeebe.client.api.events.WorkflowInstanceEvent
import io.zeebe.client.api.subscription.JobHandler
import org.slf4j.LoggerFactory
import java.time.Duration
import javax.inject.Inject

class ZeebeProvider: Zeebe {
  @Inject
  lateinit var client: ZeebeClient
  val log = LoggerFactory.getLogger(javaClass)

  override fun createWorkflowInstance(processId: String, payload: Map<String, Any>): ZeebeFuture<WorkflowInstanceEvent> {
    val wf = client.workflowClient()
    return wf.newCreateInstanceCommand().
      bpmnProcessId(processId).
      latestVersion().
      payload(payload).
      send()
  }

  override fun getWorkflow(processId: String): ZeebeFuture<WorkflowResource> {
    val wf = client.workflowClient()
    return wf.newResourceRequest().bpmnProcessId(processId).latestVersion().send()
  }

  override fun createJobClient(jobType: String, handler: JobHandler) {
    client.jobClient().
      newWorker().
      jobType(jobType).
      handler(handler).
      timeout(Duration.ofMillis(1000)).
      bufferSize(10).
      pollInterval(Duration.ofMillis(1000)).
      open()
  }

  override fun sendMessage(messageId: String, correlationId: String) {
    client.workflowClient().newPublishMessageCommand().messageName(messageId).correlationKey(correlationId).send()
  }

  override fun deployProcessWorkflow(processId: String, resourceFiles: List<String>): ZeebeFuture<DeploymentEvent> {
    val wf = client.workflowClient()
    var cmd = wf.newDeployCommand()
    var step2: DeployWorkflowCommandStep1.DeployWorkflowCommandBuilderStep2? = null
    for (f in resourceFiles) {
      if (step2 == null) {
        step2 = cmd.addResourceFile(f)
      } else {
        step2 = step2.addResourceFile(f)
      }
    }
    return step2!!.send()
  }
}
