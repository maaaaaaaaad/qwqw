package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class CreateReviewRequest(
    val rating: Int?,
    val content: String?,
    val images: List<String>?
)
