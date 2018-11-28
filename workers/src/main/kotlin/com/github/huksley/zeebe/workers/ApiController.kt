package com.github.huksley.zeebe.workers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController {

    @GetMapping("/status")
    fun status(): String = "" + System.currentTimeMillis().hashCode()
}
