package com.mad.jellomarkserver.member.adapter.`in`.web.response

import com.mad.jellomarkserver.member.core.domain.model.Member
import java.time.Instant
import java.util.UUID

data class MemberResponse(
    val id: UUID,
    val nickname: String,
    val email: String,
    val businessRegistrationNumber: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(member: Member): MemberResponse {
            return MemberResponse(
                id = member.id.value,
                nickname = member.nickname.value,
                email = member.email.value,
                businessRegistrationNumber = member.businessRegistrationNumber?.value,
                createdAt = member.createdAt,
                updatedAt = member.updatedAt
            )
        }
    }
}
