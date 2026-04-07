package com.mad.jellomarkserver.verification.adapter.driven.ses

import com.mad.jellomarkserver.verification.port.driven.EmailSenderPort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Primary
@ConditionalOnProperty(name = ["resend.api-key"], matchIfMissing = false)
class ResendEmailSenderAdapter(
    @Value("\${resend.api-key}") private val apiKey: String,
    @Value("\${resend.from-email:noreply@jellomark.com}") private val fromEmail: String
) : EmailSenderPort {

    private val log = LoggerFactory.getLogger(ResendEmailSenderAdapter::class.java)
    private val restTemplate = RestTemplate()

    override fun send(to: String, subject: String, body: String) {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(apiKey)
        }

        val payload = mapOf(
            "from" to "JelloMark <$fromEmail>",
            "to" to listOf(to),
            "subject" to subject,
            "html" to body
        )

        val request = HttpEntity(payload, headers)
        restTemplate.postForObject(RESEND_API_URL, request, Map::class.java)
        log.info("Verification email sent to {} via Resend", to)
    }

    companion object {
        private const val RESEND_API_URL = "https://api.resend.com/emails"
    }
}
