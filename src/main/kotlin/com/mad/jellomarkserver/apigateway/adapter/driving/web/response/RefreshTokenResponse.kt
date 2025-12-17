package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
