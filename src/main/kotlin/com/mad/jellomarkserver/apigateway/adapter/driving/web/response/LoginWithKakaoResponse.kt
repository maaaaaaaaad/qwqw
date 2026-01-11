package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

data class LoginWithKakaoResponse(
    val accessToken: String,
    val refreshToken: String,
)
