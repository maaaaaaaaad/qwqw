package com.mad.jellomarkserver.reservation.core.domain.model

import java.util.*

@JvmInline
value class ReservationId private constructor(val value: UUID) {
    companion object {
        fun new(): ReservationId = ReservationId(UUID.randomUUID())
        fun from(uuid: UUID): ReservationId = ReservationId(uuid)
    }
}
