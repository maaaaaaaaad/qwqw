package com.mad.jellomarkserver.member.core.domain.model

import java.util.UUID

@JvmInline
value class MemberId private constructor(val value: UUID) {
    companion object {
        fun new(): MemberId = MemberId(UUID.randomUUID())
        fun from(value: UUID?): MemberId {
            requireNotNull(value)
            return MemberId(value)
        }
    }
}
