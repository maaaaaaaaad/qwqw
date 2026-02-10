package com.mad.jellomarkserver.usagehistory.core.domain.model

import java.util.*

@JvmInline
value class UsageHistoryId private constructor(val value: UUID) {
    companion object {
        fun new(): UsageHistoryId = UsageHistoryId(UUID.randomUUID())
        fun from(uuid: UUID): UsageHistoryId = UsageHistoryId(uuid)
    }
}
