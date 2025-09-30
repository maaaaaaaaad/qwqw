package com.mad.jellomarkserver.owner.core.domain.model

import java.util.UUID

@JvmInline
value class OwnerId private constructor(val value: UUID) {
    companion object {
        fun new(): OwnerId = OwnerId(UUID.randomUUID())
        fun from(value: UUID?): OwnerId {
            requireNotNull(value)
            return OwnerId(value)
        }
    }
}