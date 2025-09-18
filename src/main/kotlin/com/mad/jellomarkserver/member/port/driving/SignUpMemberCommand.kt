package com.mad.jellomarkserver.member.port.driving

import com.mad.jellomarkserver.member.core.domain.model.MemberType

data class SignUpMemberCommand(
    val nickname: String,
    val email: String,
    val memberType: MemberType,
    val businessRegistrationNumber: String? = null
)
