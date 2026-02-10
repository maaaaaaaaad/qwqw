package com.mad.jellomarkserver.reservation.port.driving

data class GetAvailableDatesQuery(
    val shopId: String,
    val treatmentId: String,
    val yearMonth: String
)
