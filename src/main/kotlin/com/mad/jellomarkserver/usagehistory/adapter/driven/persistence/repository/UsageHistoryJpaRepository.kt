package com.mad.jellomarkserver.usagehistory.adapter.driven.persistence.repository

import com.mad.jellomarkserver.usagehistory.adapter.driven.persistence.entity.UsageHistoryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UsageHistoryJpaRepository : JpaRepository<UsageHistoryJpaEntity, UUID> {
    fun findByMemberId(memberId: UUID): List<UsageHistoryJpaEntity>
}
