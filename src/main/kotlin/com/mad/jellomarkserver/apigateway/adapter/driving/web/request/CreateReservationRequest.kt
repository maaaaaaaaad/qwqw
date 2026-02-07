package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class CreateReservationRequest(
    val shopId: String,
    val treatmentId: String,
    val reservationDate: String,
    val startTime: String,
    val memo: String?
)
