package com.mad.jellomarkserver.owner.adapter.driving.web.response

import com.mad.jellomarkserver.owner.core.domain.model.Owner
import java.time.Instant
import java.util.*

data class OwnerResponse(
    val id: UUID,
    val businessNumber: String,
    val phoneNumber: String,
    val nickname: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(owner: Owner): OwnerResponse {
            return OwnerResponse(
                id = owner.id.value,
                businessNumber = owner.businessNumber.value,
                phoneNumber = owner.ownerPhoneNumber.value,
                nickname = owner.ownerNickname.value,
                createdAt = owner.createdAt,
                updatedAt = owner.updatedAt
            )
        }
    }
}
