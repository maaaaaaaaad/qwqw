package com.mad.jellomarkserver.verification.core.application

import com.mad.jellomarkserver.verification.core.domain.exception.InvalidVerificationCodeException
import com.mad.jellomarkserver.verification.core.domain.exception.VerificationCodeExpiredException
import com.mad.jellomarkserver.verification.core.domain.exception.VerificationCodeNotFoundException
import com.mad.jellomarkserver.verification.core.domain.model.VerificationToken
import com.mad.jellomarkserver.verification.core.domain.model.VerificationType
import com.mad.jellomarkserver.verification.port.driven.VerificationCodePort
import com.mad.jellomarkserver.verification.port.driving.VerifyCodeCommand
import com.mad.jellomarkserver.verification.port.driving.VerifyCodeUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VerifyCodeUseCaseImpl(
    private val verificationCodePort: VerificationCodePort
) : VerifyCodeUseCase {

    @Transactional
    override fun execute(command: VerifyCodeCommand): VerificationToken {
        val target = command.target.trim().lowercase()
        val type = VerificationType.valueOf(command.type.uppercase())

        val verificationCode = verificationCodePort.findLatestByTargetAndType(target, type)
            ?: throw VerificationCodeNotFoundException()

        if (verificationCode.isExpired()) {
            throw VerificationCodeExpiredException()
        }

        if (!verificationCode.matches(command.code)) {
            throw InvalidVerificationCodeException()
        }

        verificationCode.markVerified()
        verificationCodePort.save(verificationCode)

        return VerificationToken.generate()
    }
}
