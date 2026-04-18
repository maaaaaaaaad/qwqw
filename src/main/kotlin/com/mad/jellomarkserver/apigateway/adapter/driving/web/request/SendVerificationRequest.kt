package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class SendVerificationRequest(
    val target: String,
    val type: String,
    val purpose: String = "SIGNUP"
)
