package zeebe.workers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import javax.inject.Inject

@Controller("/")
class ApiController {

  @Inject
  lateinit var meta: MetaSink

  @Get("/status")
  fun status() = meta
}
