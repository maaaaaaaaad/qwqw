package com.mad.jellomarkserver.auth.core.domain.model

import java.util.*

@JvmInline
value class RefreshTokenId private constructor(val value: UUID) {
    companion object {
        fun new(): RefreshTokenId = RefreshTokenId(UUID.randomUUID())
        fun from(value: UUID?): RefreshTokenId {
            requireNotNull(value)
            return RefreshTokenId(value)
        }
    }
}
