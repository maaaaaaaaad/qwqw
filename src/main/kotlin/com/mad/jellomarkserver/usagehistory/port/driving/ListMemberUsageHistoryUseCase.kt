package com.mad.jellomarkserver.usagehistory.port.driving

import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory

data class ListMemberUsageHistoryCommand(
    val memberId: String
)

fun interface ListMemberUsageHistoryUseCase {
    fun execute(command: ListMemberUsageHistoryCommand): List<UsageHistory>
}
