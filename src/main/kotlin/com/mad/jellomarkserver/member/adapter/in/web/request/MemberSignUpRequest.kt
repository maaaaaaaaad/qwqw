package com.mad.jellomarkserver.member.adapter.`in`.web.request

data class MemberSignUpRequest(
    val nickname: String,
    val email: String,
    val businessRegistrationNumber: String? = null
)