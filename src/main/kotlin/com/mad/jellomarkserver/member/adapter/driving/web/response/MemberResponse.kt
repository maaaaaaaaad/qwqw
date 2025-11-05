package com.mad.jellomarkserver.member.adapter.driving.web.response

import com.mad.jellomarkserver.member.core.domain.model.Member
import java.time.Instant
import java.util.UUID

data class MemberResponse(
    val id: UUID,
    val nickname: String,
    val email: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(member: Member): MemberResponse {
            return MemberResponse(
                id = member.id.value,
                nickname = member.memberNickname.value,
                email = member.memberEmail.value,
                createdAt = member.createdAt,
                updatedAt = member.updatedAt
            )
        }
    }
}
