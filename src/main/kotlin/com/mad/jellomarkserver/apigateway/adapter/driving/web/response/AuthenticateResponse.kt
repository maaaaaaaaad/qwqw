package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

data class AuthenticateResponse(
    val authenticated: Boolean,
    val email: String?,
    val userType: String?,
    val accessToken: String?,
    val refreshToken: String?
)
