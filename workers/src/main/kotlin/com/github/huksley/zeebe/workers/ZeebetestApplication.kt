package com.github.huksley.zeebe.workers

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableScheduling
class ZeebetestApplication {
    companion object {
        val console = LoggerFactory.getLogger(ZeebetestApplication::class.java.name)
    }
}

fun main(args: Array<String>) {
    runApplication<ZeebetestApplication>(*args)
}
