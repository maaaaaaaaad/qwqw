package com.mad.jellomarkserver.owner.core.domain.model

import java.time.Instant

class Owner private constructor(
    val id: OwnerId,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun create(): Owner {
            val now = Instant.now()
            return Owner(OwnerId.new(), now, now)
        }

        fun reconstruct(id: OwnerId, createdAt: Instant, updatedAt: Instant): Owner {
            return Owner(id, createdAt, updatedAt)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Owner) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}