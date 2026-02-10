package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class RegisterDeviceTokenRequest(
    val token: String,
    val platform: String
)
