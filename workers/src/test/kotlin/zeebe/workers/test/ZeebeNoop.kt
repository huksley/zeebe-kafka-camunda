package zeebe.workers.test

import io.micronaut.context.annotation.Primary
import io.zeebe.client.api.ZeebeFuture
import io.zeebe.client.api.events.DeploymentEvent
import io.zeebe.client.api.events.WorkflowInstanceEvent
import io.zeebe.client.api.subscription.JobHandler
import org.slf4j.LoggerFactory
import zeebe.workers.Zeebe
import java.util.concurrent.TimeUnit

@Primary
class ZeebeNoop: Zeebe {
  val log = LoggerFactory.getLogger(javaClass)

  class ZeebeFutureEmpty<T>: ZeebeFuture<T> {
    override fun isDone(): Boolean {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(): T {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(timeout: Long, unit: TimeUnit?): T {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isCancelled(): Boolean {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun join(): T {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun join(p0: Long, p1: TimeUnit?): T {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

  }

  override fun deployProcessWorkflow(processId: String, resourceFile: String): ZeebeFuture<DeploymentEvent> {
    log.info("noop deployProcessWorkflow()")
    return ZeebeFutureEmpty<DeploymentEvent>()
  }

  override fun createWorkflowInstance(processId: String, payload: Map<String, Any>): ZeebeFuture<WorkflowInstanceEvent> {
    log.info("noop createWorkflowInstance()")
    return ZeebeFutureEmpty<WorkflowInstanceEvent>()
  }

  override fun createJobClient(jobType: String, handler: JobHandler) {
    log.info("noop createJobClient()")
  }
}
