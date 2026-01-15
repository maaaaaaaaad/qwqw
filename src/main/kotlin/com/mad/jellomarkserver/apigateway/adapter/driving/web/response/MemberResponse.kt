package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.member.core.domain.model.Member
import java.time.Instant
import java.util.*

data class MemberResponse(
    val id: UUID,
    val socialProvider: String,
    val socialId: String,
    val nickname: String,
    val displayName: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(member: Member): MemberResponse {
            return MemberResponse(
                id = member.id.value,
                socialProvider = member.socialProvider.name,
                socialId = member.socialId.value,
                nickname = member.memberNickname.value,
                displayName = member.displayName.value,
                createdAt = member.createdAt,
                updatedAt = member.updatedAt
            )
        }
    }
}
