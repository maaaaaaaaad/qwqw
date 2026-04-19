package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class WithdrawOwnerRequest(
    val password: String,
    val reason: String,
    val verificationToken: String
)
