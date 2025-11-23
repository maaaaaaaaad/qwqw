package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.owner.core.domain.model.Owner
import java.time.Instant
import java.util.*

data class SignUpResponse(
    val id: UUID,
    val userType: String,
    val nickname: String?,
    val email: String?,
    val businessNumber: String?,
    val phoneNumber: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromMember(member: Member): SignUpResponse {
            return SignUpResponse(
                id = member.id.value,
                userType = "MEMBER",
                nickname = member.memberNickname.value,
                email = member.memberEmail.value,
                businessNumber = null,
                phoneNumber = null,
                createdAt = member.createdAt,
                updatedAt = member.updatedAt
            )
        }

        fun fromOwner(owner: Owner): SignUpResponse {
            return SignUpResponse(
                id = owner.id.value,
                userType = "OWNER",
                nickname = owner.ownerNickname.value,
                email = null,
                businessNumber = owner.businessNumber.value,
                phoneNumber = owner.ownerPhoneNumber.value,
                createdAt = owner.createdAt,
                updatedAt = owner.updatedAt
            )
        }
    }
}
