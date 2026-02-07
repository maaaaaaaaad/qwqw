package com.mad.jellomarkserver.reservation.port.driving

data class NoShowReservationCommand(
    val reservationId: String,
    val ownerId: String
)
