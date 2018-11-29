package zeebe.workers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/")
class ApiController {
  @Get("/")
  fun status(): String = "OK"
}
