package com.mad.jellomarkserver.usagehistory.port.driven

import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory

interface UsageHistoryPort {
    fun save(usageHistory: UsageHistory): UsageHistory
    fun findByMemberId(memberId: MemberId): List<UsageHistory>
}
