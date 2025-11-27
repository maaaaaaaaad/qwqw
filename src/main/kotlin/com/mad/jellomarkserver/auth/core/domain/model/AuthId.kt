package com.mad.jellomarkserver.auth.core.domain.model

import java.util.*

@JvmInline
value class AuthId private constructor(val value: UUID) {
    companion object {
        fun new(): AuthId = AuthId(UUID.randomUUID())
        fun from(value: UUID?): AuthId {
            requireNotNull(value)
            return AuthId(value)
        }
    }
}
