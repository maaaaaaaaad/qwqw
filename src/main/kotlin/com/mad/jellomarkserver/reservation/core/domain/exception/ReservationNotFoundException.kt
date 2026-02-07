package com.mad.jellomarkserver.reservation.core.domain.exception

class ReservationNotFoundException(reservationId: String) : RuntimeException(
    "Reservation not found: $reservationId"
)
