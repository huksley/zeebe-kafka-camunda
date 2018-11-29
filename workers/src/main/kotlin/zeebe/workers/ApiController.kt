package zeebe.workers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/")
class ApiController {
  @Get("/status")
  fun status(): String = "OK"
}
