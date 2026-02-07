package com.mad.jellomarkserver.reservation.core.domain.exception

class InvalidReservationStatusTransitionException(from: String, to: String) : RuntimeException(
    "Cannot transition reservation status from $from to $to"
)
