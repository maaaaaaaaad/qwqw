package com.mad.jellomarkserver.usagehistory.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.usagehistory.adapter.driven.persistence.entity.UsageHistoryJpaEntity
import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory

interface UsageHistoryMapper {
    fun toEntity(domain: UsageHistory): UsageHistoryJpaEntity
    fun toDomain(entity: UsageHistoryJpaEntity): UsageHistory
}
