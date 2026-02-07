package com.mad.jellomarkserver.reservation.core.domain.exception

class UnauthorizedReservationAccessException(reservationId: String, userId: String) : RuntimeException(
    "User $userId is not authorized to access reservation $reservationId"
)
