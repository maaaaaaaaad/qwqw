package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory
import java.time.Instant

data class UsageHistoryResponse(
    val id: String,
    val memberId: String,
    val shopId: String,
    val reservationId: String,
    val shopName: String,
    val treatmentName: String,
    val treatmentPrice: Int,
    val treatmentDuration: Int,
    val completedAt: Instant,
    val createdAt: Instant
) {
    companion object {
        fun from(usageHistory: UsageHistory): UsageHistoryResponse {
            return UsageHistoryResponse(
                id = usageHistory.id.value.toString(),
                memberId = usageHistory.memberId.value.toString(),
                shopId = usageHistory.shopId.value.toString(),
                reservationId = usageHistory.reservationId.value.toString(),
                shopName = usageHistory.shopName,
                treatmentName = usageHistory.treatmentName,
                treatmentPrice = usageHistory.treatmentPrice,
                treatmentDuration = usageHistory.treatmentDuration,
                completedAt = usageHistory.completedAt,
                createdAt = usageHistory.createdAt
            )
        }
    }
}
