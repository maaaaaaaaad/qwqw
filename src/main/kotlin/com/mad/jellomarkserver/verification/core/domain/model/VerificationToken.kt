package com.mad.jellomarkserver.verification.core.domain.model

import java.util.UUID

@JvmInline
value class VerificationToken(val value: String) {
    companion object {
        fun generate(): VerificationToken = VerificationToken(UUID.randomUUID().toString())
    }
}
