package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class VerifyCodeRequest(
    val target: String,
    val code: String,
    val type: String
)
