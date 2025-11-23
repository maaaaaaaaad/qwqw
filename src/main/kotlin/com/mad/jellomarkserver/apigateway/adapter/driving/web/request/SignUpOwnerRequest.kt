package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class SignUpOwnerRequest(
    val businessNumber: String,
    val phoneNumber: String,
    val nickname: String,
)
