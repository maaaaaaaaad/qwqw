package com.mad.jellomarkserver.owner.core.domain.model

import java.time.Clock
import java.time.Instant

class Owner private constructor(
    val id: OwnerId,
    val businessNumber: BusinessNumber,
    val ownerPhoneNumber: OwnerPhoneNumber,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun create(businessNumber: BusinessNumber, ownerPhoneNumber: OwnerPhoneNumber, clock: Clock = Clock.systemUTC()): Owner {
            val now = Instant.now(clock)
            return Owner(OwnerId.new(), businessNumber, ownerPhoneNumber, now, now)
        }

        fun reconstruct(id: OwnerId, businessNumber: BusinessNumber, ownerPhoneNumber: OwnerPhoneNumber, createdAt: Instant, updatedAt: Instant): Owner {
            return Owner(id, businessNumber, ownerPhoneNumber, createdAt, updatedAt)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Owner) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}