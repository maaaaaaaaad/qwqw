package com.mad.jellomarkserver.notification.core.domain.model

import java.time.Clock
import java.time.Instant
import java.util.*

class DeviceToken private constructor(
    val id: DeviceTokenId,
    val userId: UUID,
    val userRole: UserRole,
    val token: String,
    val platform: DevicePlatform,
    val createdAt: Instant
) {
    companion object {
        fun create(
            userId: UUID,
            userRole: UserRole,
            token: String,
            platform: DevicePlatform,
            clock: Clock = Clock.systemUTC()
        ): DeviceToken {
            return DeviceToken(
                id = DeviceTokenId.new(),
                userId = userId,
                userRole = userRole,
                token = token,
                platform = platform,
                createdAt = Instant.now(clock)
            )
        }

        fun reconstruct(
            id: DeviceTokenId,
            userId: UUID,
            userRole: UserRole,
            token: String,
            platform: DevicePlatform,
            createdAt: Instant
        ): DeviceToken {
            return DeviceToken(
                id = id,
                userId = userId,
                userRole = userRole,
                token = token,
                platform = platform,
                createdAt = createdAt
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeviceToken) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
