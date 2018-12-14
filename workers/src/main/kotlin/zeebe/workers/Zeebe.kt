package zeebe.workers

import io.zeebe.client.api.ZeebeFuture
import io.zeebe.client.api.commands.WorkflowResource
import io.zeebe.client.api.events.DeploymentEvent
import io.zeebe.client.api.events.WorkflowInstanceEvent
import io.zeebe.client.api.subscription.JobHandler

interface Zeebe {
  /**
   * Deploys new version of workflow
   */
  fun deployProcessWorkflow(processId: String, resourceFile: String): ZeebeFuture<DeploymentEvent>

  fun createWorkflowInstance(processId: String, payload: Map<String,Any>): ZeebeFuture<WorkflowInstanceEvent>

  fun createJobClient(jobType: String, handler: JobHandler)

  fun getWorkflow(processId: String): ZeebeFuture<WorkflowResource>
}
