package com.mad.jellomarkserver.reservation.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.adapter.driven.persistence.entity.ReservationJpaEntity
import com.mad.jellomarkserver.reservation.core.domain.model.*
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import org.springframework.stereotype.Component

@Component
class ReservationMapperImpl : ReservationMapper {

    override fun toEntity(domain: Reservation): ReservationJpaEntity {
        return ReservationJpaEntity(
            id = domain.id.value,
            shopId = domain.shopId.value,
            memberId = domain.memberId.value,
            treatmentId = domain.treatmentId.value,
            reservationDate = domain.reservationDate,
            startTime = domain.startTime,
            endTime = domain.endTime,
            status = domain.status.name,
            memo = domain.memo?.value,
            rejectionReason = domain.rejectionReason?.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: ReservationJpaEntity): Reservation {
        return Reservation.reconstruct(
            id = ReservationId.from(entity.id),
            shopId = ShopId.from(entity.shopId),
            memberId = MemberId.from(entity.memberId),
            treatmentId = TreatmentId.from(entity.treatmentId),
            reservationDate = entity.reservationDate,
            startTime = entity.startTime,
            endTime = entity.endTime,
            status = ReservationStatus.valueOf(entity.status),
            memo = entity.memo?.let { ReservationMemo.of(it) },
            rejectionReason = entity.rejectionReason?.let { RejectionReason.of(it) },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
