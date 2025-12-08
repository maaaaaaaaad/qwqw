package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class AuthenticateRequest(
    val email: String,
    val password: String,
)
