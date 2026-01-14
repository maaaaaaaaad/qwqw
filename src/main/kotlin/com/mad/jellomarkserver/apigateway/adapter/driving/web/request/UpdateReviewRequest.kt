package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class UpdateReviewRequest(
    val rating: Int?,
    val content: String?,
    val images: List<String>?
)
