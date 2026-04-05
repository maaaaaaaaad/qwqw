package com.mad.jellomarkserver.verification.adapter.driven.ses

import com.mad.jellomarkserver.verification.port.driven.EmailSenderPort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.Body
import software.amazon.awssdk.services.ses.model.Content
import software.amazon.awssdk.services.ses.model.Destination
import software.amazon.awssdk.services.ses.model.Message
import software.amazon.awssdk.services.ses.model.SendEmailRequest

@Component
@Primary
@ConditionalOnBean(SesClient::class)
class SesEmailSenderAdapter(
    private val sesClient: SesClient,
    @Value("\${aws.ses.sender-email}") private val senderEmail: String
) : EmailSenderPort {

    private val log = LoggerFactory.getLogger(SesEmailSenderAdapter::class.java)

    override fun send(to: String, subject: String, body: String) {
        val request = SendEmailRequest.builder()
            .source(senderEmail)
            .destination(Destination.builder().toAddresses(to).build())
            .message(
                Message.builder()
                    .subject(Content.builder().charset("UTF-8").data(subject).build())
                    .body(Body.builder().html(Content.builder().charset("UTF-8").data(body).build()).build())
                    .build()
            )
            .build()

        sesClient.sendEmail(request)
        log.info("Verification email sent to {}", to)
    }
}
