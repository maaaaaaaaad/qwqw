package com.mad.jellomarkserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class JelloMarkServerApplication

fun main(args: Array<String>) {
    runApplication<JelloMarkServerApplication>(*args)
}
