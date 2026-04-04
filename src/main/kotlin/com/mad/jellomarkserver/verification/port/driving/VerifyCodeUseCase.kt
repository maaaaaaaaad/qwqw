package com.mad.jellomarkserver.verification.port.driving

import com.mad.jellomarkserver.verification.core.domain.model.VerificationToken

fun interface VerifyCodeUseCase {
    fun execute(command: VerifyCodeCommand): VerificationToken
}

data class VerifyCodeCommand(
    val target: String,
    val code: String,
    val type: String
)
