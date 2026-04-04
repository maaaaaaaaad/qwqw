package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

data class VerificationResponse(
    val verified: Boolean,
    val verificationToken: String? = null
)
