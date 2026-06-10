package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class LoginWithAppleRequest(
    val identityToken: String,
    val fullName: String? = null
)
