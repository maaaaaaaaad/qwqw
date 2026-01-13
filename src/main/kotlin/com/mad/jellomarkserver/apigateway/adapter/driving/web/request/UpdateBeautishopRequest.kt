package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class UpdateBeautishopRequest(
    val operatingTime: Map<String, String>?,
    val shopDescription: String?,
    val shopImage: String?
)
