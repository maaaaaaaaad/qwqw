package com.mad.jellomarkserver.domain.model.member

import java.util.UUID

data class MemberId private constructor(val value: UUID) {
    companion object {
        fun new(): MemberId = MemberId(UUID.randomUUID())
        fun from(value: UUID?): MemberId {
            requireNotNull(value)
            return MemberId(value)
        }
    }
}
