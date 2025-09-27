package com.mad.jellomarkserver.member.port.driving

data class SignUpMemberCommand(
    val nickname: String,
    val email: String,
    val businessRegistrationNumber: String? = null
)
