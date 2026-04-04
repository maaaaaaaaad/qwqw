package com.mad.jellomarkserver.verification.port.driven

interface EmailSenderPort {
    fun send(to: String, subject: String, body: String)
}
