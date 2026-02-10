package com.mad.jellomarkserver.notification.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.notification.adapter.driven.persistence.entity.DeviceTokenJpaEntity
import com.mad.jellomarkserver.notification.core.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class DeviceTokenMapperImplTest {

    private val mapper = DeviceTokenMapperImpl()

    @Test
    fun `should map domain to entity`() {
        val id = DeviceTokenId.new()
        val userId = UUID.randomUUID()
        val createdAt = Instant.parse("2025-06-15T14:00:00Z")

        val domain = DeviceToken.reconstruct(
            id = id,
            userId = userId,
            userRole = UserRole.MEMBER,
            token = "fcm-token-123",
            platform = DevicePlatform.IOS,
            createdAt = createdAt
        )

        val entity = mapper.toEntity(domain)

        assertEquals(id.value, entity.id)
        assertEquals(userId, entity.userId)
        assertEquals("MEMBER", entity.userRole)
        assertEquals("fcm-token-123", entity.token)
        assertEquals("IOS", entity.platform)
        assertEquals(createdAt, entity.createdAt)
    }

    @Test
    fun `should map entity to domain`() {
        val id = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val createdAt = Instant.parse("2025-06-15T14:00:00Z")

        val entity = DeviceTokenJpaEntity(
            id = id,
            userId = userId,
            userRole = "OWNER",
            token = "fcm-token-456",
            platform = "ANDROID",
            createdAt = createdAt
        )

        val domain = mapper.toDomain(entity)

        assertEquals(id, domain.id.value)
        assertEquals(userId, domain.userId)
        assertEquals(UserRole.OWNER, domain.userRole)
        assertEquals("fcm-token-456", domain.token)
        assertEquals(DevicePlatform.ANDROID, domain.platform)
        assertEquals(createdAt, domain.createdAt)
    }

    @Test
    fun `should round-trip domain to entity and back`() {
        val original = DeviceToken.reconstruct(
            id = DeviceTokenId.new(),
            userId = UUID.randomUUID(),
            userRole = UserRole.MEMBER,
            token = "round-trip-token",
            platform = DevicePlatform.IOS,
            createdAt = Instant.parse("2025-06-15T14:00:00Z")
        )

        val entity = mapper.toEntity(original)
        val result = mapper.toDomain(entity)

        assertEquals(original.id, result.id)
        assertEquals(original.userId, result.userId)
        assertEquals(original.userRole, result.userRole)
        assertEquals(original.token, result.token)
        assertEquals(original.platform, result.platform)
        assertEquals(original.createdAt, result.createdAt)
    }
}
