package com.mad.jellomarkserver.owner.adapter.driving.web.request

data class OwnerSignUpRequest(
    val businessNumber: String,
    val phoneNumber: String,
    val nickname: String,
    val email: String,
    val password: String,
)