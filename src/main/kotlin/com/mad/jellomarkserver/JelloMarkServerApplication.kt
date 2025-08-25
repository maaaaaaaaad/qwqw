package com.mad.jellomarkserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JelloMarkServerApplication

fun main(args: Array<String>) {
    runApplication<JelloMarkServerApplication>(*args)
}
