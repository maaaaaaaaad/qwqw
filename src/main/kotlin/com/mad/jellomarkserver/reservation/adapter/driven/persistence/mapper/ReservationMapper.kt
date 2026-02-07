package com.mad.jellomarkserver.reservation.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.reservation.adapter.driven.persistence.entity.ReservationJpaEntity
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation

interface ReservationMapper {
    fun toEntity(domain: Reservation): ReservationJpaEntity
    fun toDomain(entity: ReservationJpaEntity): Reservation
}
