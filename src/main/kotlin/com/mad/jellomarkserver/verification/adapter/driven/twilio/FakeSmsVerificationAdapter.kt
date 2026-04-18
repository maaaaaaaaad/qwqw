package com.mad.jellomarkserver.verification.adapter.driven.twilio

import com.mad.jellomarkserver.verification.port.driven.SmsVerificationPort
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnMissingBean(TwilioVerifyAdapter::class)
class FakeSmsVerificationAdapter : SmsVerificationPort {

    private val log = LoggerFactory.getLogger(FakeSmsVerificationAdapter::class.java)

    override fun sendCode(phoneNumber: String) {
        log.warn("SMS verification not configured. Skipping send to {}", phoneNumber)
    }

    override fun checkCode(phoneNumber: String, code: String): Boolean {
        log.warn("SMS verification not configured. Auto-approving code for {}", phoneNumber)
        return true
    }
}
