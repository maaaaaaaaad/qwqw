package com.mad.jellomarkserver.usagehistory.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import java.time.Clock
import java.time.Instant

class UsageHistory private constructor(
    val id: UsageHistoryId,
    val memberId: MemberId,
    val shopId: ShopId,
    val reservationId: ReservationId,
    val shopName: String,
    val treatmentName: String,
    val treatmentPrice: Int,
    val treatmentDuration: Int,
    val completedAt: Instant,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            shopId: ShopId,
            reservationId: ReservationId,
            shopName: String,
            treatmentName: String,
            treatmentPrice: Int,
            treatmentDuration: Int,
            completedAt: Instant,
            clock: Clock = Clock.systemUTC()
        ): UsageHistory {
            return UsageHistory(
                id = UsageHistoryId.new(),
                memberId = memberId,
                shopId = shopId,
                reservationId = reservationId,
                shopName = shopName,
                treatmentName = treatmentName,
                treatmentPrice = treatmentPrice,
                treatmentDuration = treatmentDuration,
                completedAt = completedAt,
                createdAt = Instant.now(clock)
            )
        }

        fun reconstruct(
            id: UsageHistoryId,
            memberId: MemberId,
            shopId: ShopId,
            reservationId: ReservationId,
            shopName: String,
            treatmentName: String,
            treatmentPrice: Int,
            treatmentDuration: Int,
            completedAt: Instant,
            createdAt: Instant
        ): UsageHistory {
            return UsageHistory(
                id = id,
                memberId = memberId,
                shopId = shopId,
                reservationId = reservationId,
                shopName = shopName,
                treatmentName = treatmentName,
                treatmentPrice = treatmentPrice,
                treatmentDuration = treatmentDuration,
                completedAt = completedAt,
                createdAt = createdAt
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsageHistory) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
