package com.mad.jellomarkserver.reservation.port.driving

data class CreateReservationCommand(
    val shopId: String,
    val memberId: String,
    val treatmentId: String,
    val reservationDate: String,
    val startTime: String,
    val memo: String?
)
