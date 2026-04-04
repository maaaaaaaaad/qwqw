package com.mad.jellomarkserver.verification.core.domain.model

import java.time.Instant
import java.util.UUID

class VerificationCode private constructor(
    val id: UUID,
    val target: String,
    val code: String,
    val type: VerificationType,
    val expiresAt: Instant,
    var verified: Boolean,
    val createdAt: Instant
) {
    companion object {
        private const val CODE_LENGTH = 6
        private const val EXPIRY_MINUTES = 5L

        fun create(target: String, type: VerificationType): VerificationCode {
            val code = (0 until CODE_LENGTH)
                .map { (0..9).random() }
                .joinToString("")

            val now = Instant.now()
            return VerificationCode(
                id = UUID.randomUUID(),
                target = target.trim().lowercase(),
                code = code,
                type = type,
                expiresAt = now.plusSeconds(EXPIRY_MINUTES * 60),
                verified = false,
                createdAt = now
            )
        }

        fun reconstitute(
            id: UUID,
            target: String,
            code: String,
            type: VerificationType,
            expiresAt: Instant,
            verified: Boolean,
            createdAt: Instant
        ): VerificationCode = VerificationCode(id, target, code, type, expiresAt, verified, createdAt)
    }

    fun isExpired(): Boolean = Instant.now().isAfter(expiresAt)

    fun matches(inputCode: String): Boolean = code == inputCode.trim()

    fun markVerified() {
        verified = true
    }
}
