package com.github.huksley.zeebe

import io.micronaut.http.annotation.Controller

@Controller("/")
class ApiController {
    fun status(): String = "OK"
}
