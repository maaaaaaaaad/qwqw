package com.mad.jellomarkserver.reservation.port.driving

data class CompleteReservationCommand(
    val reservationId: String,
    val ownerId: String
)
