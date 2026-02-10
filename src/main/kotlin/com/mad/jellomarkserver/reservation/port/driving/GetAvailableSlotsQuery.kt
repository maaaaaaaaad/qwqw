package com.mad.jellomarkserver.reservation.port.driving

data class GetAvailableSlotsQuery(
    val shopId: String,
    val treatmentId: String,
    val date: String
)
