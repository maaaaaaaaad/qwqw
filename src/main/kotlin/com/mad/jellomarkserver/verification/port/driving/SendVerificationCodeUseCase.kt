package com.mad.jellomarkserver.verification.port.driving

fun interface SendVerificationCodeUseCase {
    fun execute(command: SendVerificationCodeCommand)
}

data class SendVerificationCodeCommand(
    val target: String,
    val type: String
)
