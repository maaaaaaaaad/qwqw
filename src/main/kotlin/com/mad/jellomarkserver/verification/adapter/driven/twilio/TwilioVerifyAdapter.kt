package com.mad.jellomarkserver.verification.adapter.driven.twilio

import com.mad.jellomarkserver.verification.port.driven.SmsVerificationPort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.*

@Component
@ConditionalOnProperty(name = ["twilio.account-sid"], matchIfMissing = false)
class TwilioVerifyAdapter(
    @Value("\${twilio.account-sid}") private val accountSid: String,
    @Value("\${twilio.auth-token}") private val authToken: String,
    @Value("\${twilio.verify-service-sid}") private val verifyServiceSid: String
) : SmsVerificationPort {

    private val log = LoggerFactory.getLogger(TwilioVerifyAdapter::class.java)
    private val restTemplate = RestTemplate()

    override fun sendCode(phoneNumber: String) {
        val url = "$BASE_URL/Services/$verifyServiceSid/Verifications"

        val body = LinkedMultiValueMap<String, String>().apply {
            add("To", formatPhoneNumber(phoneNumber))
            add("Channel", "sms")
            add("Locale", "ko")
        }

        val request = HttpEntity(body, buildHeaders())

        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.postForObject(url, request, Map::class.java) as Map<String, Any>
        val status = response["status"] as? String

        log.info("Twilio Verify SMS sent to {}: status={}", phoneNumber, status)
    }

    override fun checkCode(phoneNumber: String, code: String): Boolean {
        val url = "$BASE_URL/Services/$verifyServiceSid/VerificationCheck"

        val body = LinkedMultiValueMap<String, String>().apply {
            add("To", formatPhoneNumber(phoneNumber))
            add("Code", code)
        }

        val request = HttpEntity(body, buildHeaders())

        return try {
            @Suppress("UNCHECKED_CAST")
            val response = restTemplate.postForObject(url, request, Map::class.java) as Map<String, Any>
            val status = response["status"] as? String
            log.info("Twilio Verify check for {}: status={}", phoneNumber, status)
            status == "approved"
        } catch (e: Exception) {
            log.warn("Twilio Verify check failed for {}: {}", phoneNumber, e.message)
            false
        }
    }

    private fun buildHeaders(): HttpHeaders {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            val credentials = "$accountSid:$authToken"
            val encoded = Base64.getEncoder().encodeToString(credentials.toByteArray())
            set("Authorization", "Basic $encoded")
        }
    }

    private fun formatPhoneNumber(phone: String): String {
        val digits = phone.replace(Regex("[^0-9]"), "")
        if (digits.startsWith("82")) return "+$digits"
        if (digits.startsWith("010") || digits.startsWith("011")) return "+82${digits.substring(1)}"
        return "+$digits"
    }

    companion object {
        private const val BASE_URL = "https://verify.twilio.com/v2"
    }
}
