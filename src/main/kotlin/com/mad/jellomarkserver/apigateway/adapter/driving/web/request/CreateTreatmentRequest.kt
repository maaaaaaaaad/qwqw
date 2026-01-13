package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class CreateTreatmentRequest(
    val treatmentName: String,
    val price: Int,
    val duration: Int,
    val description: String?
)
