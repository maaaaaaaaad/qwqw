package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

data class LoginWithAppleResponse(
    val accessToken: String,
    val refreshToken: String
)
