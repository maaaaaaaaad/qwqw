package com.mad.jellomarkserver.member.adapter.`in`.web.request

import com.mad.jellomarkserver.member.core.domain.model.MemberType

data class MemberSignUpRequest(
    val nickname: String,
    val email: String,
    val memberType: MemberType,
    val businessRegistrationNumber: String? = null
)