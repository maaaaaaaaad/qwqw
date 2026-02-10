package com.mad.jellomarkserver.usagehistory.adapter.driven.persistence.repository

import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.usagehistory.adapter.driven.persistence.mapper.UsageHistoryMapper
import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory
import com.mad.jellomarkserver.usagehistory.port.driven.UsageHistoryPort
import org.springframework.stereotype.Component

@Component
class UsageHistoryPersistenceAdapter(
    private val repository: UsageHistoryJpaRepository,
    private val mapper: UsageHistoryMapper
) : UsageHistoryPort {

    override fun save(usageHistory: UsageHistory): UsageHistory {
        val entity = mapper.toEntity(usageHistory)
        val saved = repository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun findByMemberId(memberId: MemberId): List<UsageHistory> {
        return repository.findByMemberId(memberId.value)
            .map { mapper.toDomain(it) }
    }
}
