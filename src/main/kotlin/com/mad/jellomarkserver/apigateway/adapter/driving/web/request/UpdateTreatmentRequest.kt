package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class UpdateTreatmentRequest(
    val treatmentName: String,
    val price: Int,
    val duration: Int,
    val description: String?
)
