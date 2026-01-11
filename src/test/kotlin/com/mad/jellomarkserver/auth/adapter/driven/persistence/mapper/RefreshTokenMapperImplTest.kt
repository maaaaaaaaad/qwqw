package com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.RefreshTokenJpaEntity
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken
import com.mad.jellomarkserver.auth.core.domain.model.RefreshTokenId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class RefreshTokenMapperImplTest {

    private val refreshTokenMapper = RefreshTokenMapperImpl()

    @Test
    fun `should correctly map RefreshTokenJpaEntity to RefreshToken`() {
        val id = UUID.randomUUID()
        val identifier = "test@example.com"
        val userType = "OWNER"
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.test"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val refreshTokenJpaEntity = RefreshTokenJpaEntity(
            id = id,
            identifier = identifier,
            userType = userType,
            token = token,
            expiresAt = expiresAt,
            createdAt = createdAt
        )

        val result = refreshTokenMapper.toDomain(refreshTokenJpaEntity)

        assertEquals(RefreshTokenId.from(id), result.id)
        assertEquals(identifier, result.identifier)
        assertEquals(userType, result.userType)
        assertEquals(token, result.token)
        assertEquals(expiresAt, result.expiresAt)
        assertEquals(createdAt, result.createdAt)
    }

    @Test
    fun `should correctly map RefreshToken with socialId identifier`() {
        val id = UUID.randomUUID()
        val identifier = "3456789012345"
        val userType = "MEMBER"
        val token = "valid.jwt.token"
        val expiresAt = Instant.EPOCH
        val createdAt = Instant.EPOCH
        val refreshTokenJpaEntity = RefreshTokenJpaEntity(
            id = id,
            identifier = identifier,
            userType = userType,
            token = token,
            expiresAt = expiresAt,
            createdAt = createdAt
        )

        val result = refreshTokenMapper.toDomain(refreshTokenJpaEntity)

        assertEquals(RefreshTokenId.from(id), result.id)
        assertEquals(identifier, result.identifier)
        assertEquals(userType, result.userType)
        assertEquals(token, result.token)
        assertEquals(expiresAt, result.expiresAt)
        assertEquals(createdAt, result.createdAt)
    }

    @Test
    fun `should correctly map RefreshToken to RefreshTokenJpaEntity`() {
        val id = RefreshTokenId.from(UUID.randomUUID())
        val identifier = "test@example.com"
        val userType = "OWNER"
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.test"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val domain = RefreshToken.reconstruct(id, identifier, userType, token, expiresAt, createdAt)

        val entity = refreshTokenMapper.toEntity(domain)

        assertEquals(id.value, entity.id)
        assertEquals(identifier, entity.identifier)
        assertEquals(userType, entity.userType)
        assertEquals(token, entity.token)
        assertEquals(expiresAt, entity.expiresAt)
        assertEquals(createdAt, entity.createdAt)
    }

    @Test
    fun `domain - entity - domain round-trip should keep values`() {
        val id = RefreshTokenId.from(UUID.randomUUID())
        val identifier = "test@example.com"
        val userType = "OWNER"
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.test"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val original = RefreshToken.reconstruct(id, identifier, userType, token, expiresAt, createdAt)

        val entity = refreshTokenMapper.toEntity(original)
        val roundTripped = refreshTokenMapper.toDomain(entity)

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.identifier, roundTripped.identifier)
        assertEquals(original.userType, roundTripped.userType)
        assertEquals(original.token, roundTripped.token)
        assertEquals(original.expiresAt, roundTripped.expiresAt)
        assertEquals(original.createdAt, roundTripped.createdAt)
    }

    @Test
    fun `should correctly map MEMBER userType`() {
        val id = RefreshTokenId.from(UUID.randomUUID())
        val identifier = "123456789"
        val userType = "MEMBER"
        val token = "valid.jwt.token"
        val expiresAt = Instant.EPOCH
        val createdAt = Instant.EPOCH
        val domain = RefreshToken.reconstruct(id, identifier, userType, token, expiresAt, createdAt)

        val entity = refreshTokenMapper.toEntity(domain)
        val roundTripped = refreshTokenMapper.toDomain(entity)

        assertEquals("MEMBER", roundTripped.userType)
        assertEquals(identifier, roundTripped.identifier)
    }
}
