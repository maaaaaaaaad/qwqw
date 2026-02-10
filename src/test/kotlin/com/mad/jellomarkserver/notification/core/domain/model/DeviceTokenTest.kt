package com.mad.jellomarkserver.notification.core.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

class DeviceTokenTest {

    private val fixedClock = Clock.fixed(Instant.parse("2025-06-15T14:00:00Z"), ZoneId.of("UTC"))

    @Test
    fun `should create device token with generated id`() {
        val userId = UUID.randomUUID()

        val token = DeviceToken.create(
            userId = userId,
            userRole = UserRole.MEMBER,
            token = "fcm-token-123",
            platform = DevicePlatform.IOS,
            clock = fixedClock
        )

        assertNotNull(token.id)
        assertEquals(userId, token.userId)
        assertEquals(UserRole.MEMBER, token.userRole)
        assertEquals("fcm-token-123", token.token)
        assertEquals(DevicePlatform.IOS, token.platform)
        assertEquals(Instant.parse("2025-06-15T14:00:00Z"), token.createdAt)
    }

    @Test
    fun `should reconstruct device token from persistence`() {
        val id = DeviceTokenId.new()
        val userId = UUID.randomUUID()
        val createdAt = Instant.parse("2025-06-10T10:00:00Z")

        val token = DeviceToken.reconstruct(
            id = id,
            userId = userId,
            userRole = UserRole.OWNER,
            token = "fcm-token-456",
            platform = DevicePlatform.ANDROID,
            createdAt = createdAt
        )

        assertEquals(id, token.id)
        assertEquals(userId, token.userId)
        assertEquals(UserRole.OWNER, token.userRole)
        assertEquals("fcm-token-456", token.token)
        assertEquals(DevicePlatform.ANDROID, token.platform)
        assertEquals(createdAt, token.createdAt)
    }

    @Test
    fun `should be equal when ids match`() {
        val id = DeviceTokenId.new()
        val token1 = DeviceToken.reconstruct(
            id = id, userId = UUID.randomUUID(), userRole = UserRole.MEMBER,
            token = "a", platform = DevicePlatform.IOS, createdAt = Instant.now()
        )
        val token2 = DeviceToken.reconstruct(
            id = id, userId = UUID.randomUUID(), userRole = UserRole.OWNER,
            token = "b", platform = DevicePlatform.ANDROID, createdAt = Instant.now()
        )

        assertEquals(token1, token2)
        assertEquals(token1.hashCode(), token2.hashCode())
    }

    @Test
    fun `should not be equal when ids differ`() {
        val token1 = DeviceToken.reconstruct(
            id = DeviceTokenId.new(), userId = UUID.randomUUID(), userRole = UserRole.MEMBER,
            token = "a", platform = DevicePlatform.IOS, createdAt = Instant.now()
        )
        val token2 = DeviceToken.reconstruct(
            id = DeviceTokenId.new(), userId = UUID.randomUUID(), userRole = UserRole.MEMBER,
            token = "a", platform = DevicePlatform.IOS, createdAt = Instant.now()
        )

        assertNotEquals(token1, token2)
    }
}
