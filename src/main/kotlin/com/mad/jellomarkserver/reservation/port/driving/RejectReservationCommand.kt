package com.mad.jellomarkserver.reservation.port.driving

data class RejectReservationCommand(
    val reservationId: String,
    val ownerId: String,
    val rejectionReason: String
)
