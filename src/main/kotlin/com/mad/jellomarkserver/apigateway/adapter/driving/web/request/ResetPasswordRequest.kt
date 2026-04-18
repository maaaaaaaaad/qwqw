package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class ResetPasswordRequest(
    val email: String,
    val newPassword: String,
    val emailVerificationToken: String
)
