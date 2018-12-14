package zeebe.workers

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Singleton
class KafkaMeta {
  var sent = AtomicInteger(0)
  var received = AtomicInteger(0)
}
