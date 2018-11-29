package zeebe.workers

import io.zeebe.client.ZeebeClient
import io.zeebe.client.api.ZeebeFuture
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

  override fun deployProcessWorkflow(processId: String, resourceFile: String): ZeebeFuture<DeploymentEvent> {
    val wf = client.workflowClient()
    return wf.newDeployCommand().
      addResourceFile(resourceFile).
      send()
  }
}
