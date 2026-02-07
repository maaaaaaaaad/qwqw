package com.mad.jellomarkserver.reservation.port.driving

data class CancelReservationCommand(
    val reservationId: String,
    val memberId: String
)
