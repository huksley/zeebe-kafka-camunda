package zeebe.workers

import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import io.micronaut.scheduling.annotation.Async
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Paths.get
import java.util.function.Predicate
import java.util.stream.Collectors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class ZeebeWorkflowDeployer {
  val log = LoggerFactory.getLogger(javaClass)

  @Inject
  lateinit var zeebe: Zeebe

  @Inject
  lateinit var eventPublisher: ApplicationEventPublisher

  @EventListener
  @Async
  open fun onStartup(event: ServerStartupEvent) {
    val fs = FileSystems.getDefault()
    val l = Files.list(fs.getPath("../test-data")).filter {
      t -> t.toAbsolutePath().toString().endsWith(".bpmn")
    }.map {
      t -> t.toAbsolutePath().toString()
    }.collect(Collectors.toList());
    log.info("Deploying workflows: {}", l)
    val ev = zeebe.deployProcessWorkflow("order-process", l).join()
    log.info("Got deployment response: ${ev}")
    for (n in l) {
      val nn = File(n.replace(".bpmn", "")).getName()
      log.info("Sending event workflow deployed {}", nn)
      eventPublisher.publishEvent(WorkflowDeployedEvent(nn))
    }
  }
}
