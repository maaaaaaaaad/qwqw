package com.mad.jellomarkserver.reservation.port.driving

data class ConfirmReservationCommand(
    val reservationId: String,
    val ownerId: String
)
