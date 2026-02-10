package com.mad.jellomarkserver.usagehistory.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.usagehistory.adapter.driven.persistence.entity.UsageHistoryJpaEntity
import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory
import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistoryId
import org.springframework.stereotype.Component

@Component
class UsageHistoryMapperImpl : UsageHistoryMapper {

    override fun toEntity(domain: UsageHistory): UsageHistoryJpaEntity {
        return UsageHistoryJpaEntity(
            id = domain.id.value,
            memberId = domain.memberId.value,
            shopId = domain.shopId.value,
            reservationId = domain.reservationId.value,
            shopName = domain.shopName,
            treatmentName = domain.treatmentName,
            treatmentPrice = domain.treatmentPrice,
            treatmentDuration = domain.treatmentDuration,
            completedAt = domain.completedAt,
            createdAt = domain.createdAt
        )
    }

    override fun toDomain(entity: UsageHistoryJpaEntity): UsageHistory {
        return UsageHistory.reconstruct(
            id = UsageHistoryId.from(entity.id),
            memberId = MemberId.from(entity.memberId),
            shopId = ShopId.from(entity.shopId),
            reservationId = ReservationId.from(entity.reservationId),
            shopName = entity.shopName,
            treatmentName = entity.treatmentName,
            treatmentPrice = entity.treatmentPrice,
            treatmentDuration = entity.treatmentDuration,
            completedAt = entity.completedAt,
            createdAt = entity.createdAt
        )
    }
}
