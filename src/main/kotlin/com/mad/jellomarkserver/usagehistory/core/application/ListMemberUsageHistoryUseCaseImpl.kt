package com.mad.jellomarkserver.usagehistory.core.application

import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory
import com.mad.jellomarkserver.usagehistory.port.driven.UsageHistoryPort
import com.mad.jellomarkserver.usagehistory.port.driving.ListMemberUsageHistoryCommand
import com.mad.jellomarkserver.usagehistory.port.driving.ListMemberUsageHistoryUseCase
import org.springframework.stereotype.Service
import java.util.*

@Service
class ListMemberUsageHistoryUseCaseImpl(
    private val usageHistoryPort: UsageHistoryPort
) : ListMemberUsageHistoryUseCase {

    override fun execute(command: ListMemberUsageHistoryCommand): List<UsageHistory> {
        val memberId = MemberId.from(UUID.fromString(command.memberId))
        return usageHistoryPort.findByMemberId(memberId)
            .sortedByDescending { it.completedAt }
    }
}
