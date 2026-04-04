package com.mad.jellomarkserver.verification.core.application

import com.mad.jellomarkserver.verification.core.domain.exception.VerificationRateLimitException
import com.mad.jellomarkserver.verification.core.domain.model.VerificationCode
import com.mad.jellomarkserver.verification.core.domain.model.VerificationType
import com.mad.jellomarkserver.verification.port.driven.EmailSenderPort
import com.mad.jellomarkserver.verification.port.driven.VerificationCodePort
import com.mad.jellomarkserver.verification.port.driving.SendVerificationCodeCommand
import com.mad.jellomarkserver.verification.port.driving.SendVerificationCodeUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SendVerificationCodeUseCaseImpl(
    private val verificationCodePort: VerificationCodePort,
    private val emailSenderPort: EmailSenderPort
) : SendVerificationCodeUseCase {

    @Transactional
    override fun execute(command: SendVerificationCodeCommand) {
        val target = command.target.trim().lowercase()
        val type = VerificationType.valueOf(command.type.uppercase())

        val recentCount = verificationCodePort.countRecentByTarget(target, RATE_LIMIT_WINDOW_MINUTES)
        if (recentCount >= MAX_REQUESTS_PER_WINDOW) {
            throw VerificationRateLimitException()
        }

        val verificationCode = VerificationCode.create(target, type)
        verificationCodePort.save(verificationCode)

        emailSenderPort.send(
            to = target,
            subject = EMAIL_SUBJECT,
            body = buildEmailBody(verificationCode.code)
        )
    }

    private fun buildEmailBody(code: String): String {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>JelloMark 이메일 인증</h2>
                <p>아래 인증코드를 입력해주세요.</p>
                <div style="font-size: 32px; font-weight: bold; color: #E91E63; padding: 20px;
                            background: #FFF0F5; border-radius: 8px; display: inline-block;">
                    $code
                </div>
                <p style="color: #888; margin-top: 16px;">이 코드는 5분간 유효합니다.</p>
            </body>
            </html>
        """.trimIndent()
    }

    companion object {
        private const val MAX_REQUESTS_PER_WINDOW = 5L
        private const val RATE_LIMIT_WINDOW_MINUTES = 60L
        private const val EMAIL_SUBJECT = "[JelloMark] 이메일 인증코드"
    }
}
