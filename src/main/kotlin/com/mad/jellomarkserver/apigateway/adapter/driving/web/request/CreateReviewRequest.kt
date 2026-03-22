package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class CreateReviewRequest(
    val reservationId: String?,
    val rating: Int?,
    val content: String?,
    val images: List<String>?
)
