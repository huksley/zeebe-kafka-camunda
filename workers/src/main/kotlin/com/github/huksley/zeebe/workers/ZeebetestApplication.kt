package com.github.huksley.zeebe.workers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ZeebetestApplication

fun main(args: Array<String>) {
    runApplication<ZeebetestApplication>(*args)
}
