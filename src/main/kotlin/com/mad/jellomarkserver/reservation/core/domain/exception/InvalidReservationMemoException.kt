package com.mad.jellomarkserver.reservation.core.domain.exception

class InvalidReservationMemoException(memo: String) : RuntimeException(
    "Invalid reservation memo: '$memo' (must be 1-200 characters)"
)
