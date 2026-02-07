package com.mad.jellomarkserver.reservation.core.domain.exception

class PastReservationException(date: String) : RuntimeException(
    "Cannot create reservation for past date: $date"
)
