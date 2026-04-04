package com.mad.jellomarkserver.verification.adapter.driven.ses

import com.mad.jellomarkserver.verification.port.driven.EmailSenderPort
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.ses.SesClient

@Component
@ConditionalOnMissingBean(SesClient::class)
class FakeEmailSenderAdapter : EmailSenderPort {

    private val log = LoggerFactory.getLogger(FakeEmailSenderAdapter::class.java)

    override fun send(to: String, subject: String, body: String) {
        log.info("[FAKE] Email to={}, subject={}", to, subject)
    }
}
