package com.mad.jellomarkserver.usagehistory.port.driving

import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory

data class CreateUsageHistoryCommand(
    val memberId: String,
    val shopId: String,
    val reservationId: String,
    val shopName: String,
    val treatmentName: String,
    val treatmentPrice: Int,
    val treatmentDuration: Int,
    val completedAt: String
)

fun interface CreateUsageHistoryUseCase {
    fun execute(command: CreateUsageHistoryCommand): UsageHistory
}
