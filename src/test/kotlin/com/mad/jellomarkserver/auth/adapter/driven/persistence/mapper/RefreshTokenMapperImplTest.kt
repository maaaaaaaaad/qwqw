package com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.RefreshTokenJpaEntity
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidAuthEmailException
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken
import com.mad.jellomarkserver.auth.core.domain.model.RefreshTokenId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertFailsWith

class RefreshTokenMapperImplTest {

    private val refreshTokenMapper = RefreshTokenMapperImpl()

    @Test
    fun `should correctly map RefreshTokenJpaEntity to RefreshToken`() {
        val id = UUID.randomUUID()
        val email = "test@example.com"
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.test"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val refreshTokenJpaEntity = RefreshTokenJpaEntity(
            id = id,
            email = email,
            token = token,
            expiresAt = expiresAt,
            createdAt = createdAt
        )

        val result = refreshTokenMapper.toDomain(refreshTokenJpaEntity)

        assertEquals(RefreshTokenId.from(id), result.id)
        assertEquals(AuthEmail.of(email), result.email)
        assertEquals(token, result.token)
        assertEquals(expiresAt, result.expiresAt)
        assertEquals(createdAt, result.createdAt)
    }

    @Test
    fun `should correctly map RefreshTokenJpaEntity with minimum valid email`() {
        val id = UUID.randomUUID()
        val email = "a@b.co"
        val token = "valid.jwt.token"
        val expiresAt = Instant.EPOCH
        val createdAt = Instant.EPOCH
        val refreshTokenJpaEntity = RefreshTokenJpaEntity(
            id = id,
            email = email,
            token = token,
            expiresAt = expiresAt,
            createdAt = createdAt
        )

        val result = refreshTokenMapper.toDomain(refreshTokenJpaEntity)

        assertEquals(RefreshTokenId.from(id), result.id)
        assertEquals(AuthEmail.of(email), result.email)
        assertEquals(token, result.token)
        assertEquals(expiresAt, result.expiresAt)
        assertEquals(createdAt, result.createdAt)
    }

    @Test
    fun `should correctly map RefreshToken to RefreshTokenJpaEntity`() {
        val id = RefreshTokenId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.test"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val domain = RefreshToken.reconstruct(id, email, token, expiresAt, createdAt)

        val entity = refreshTokenMapper.toEntity(domain)

        assertEquals(id.value, entity.id)
        assertEquals(email.value, entity.email)
        assertEquals(token, entity.token)
        assertEquals(expiresAt, entity.expiresAt)
        assertEquals(createdAt, entity.createdAt)
    }

    @Test
    fun `domain - entity - domain round-trip should keep values`() {
        val id = RefreshTokenId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.test"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val original = RefreshToken.reconstruct(id, email, token, expiresAt, createdAt)

        val entity = refreshTokenMapper.toEntity(original)
        val roundTripped = refreshTokenMapper.toDomain(entity)

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.email, roundTripped.email)
        assertEquals(original.token, roundTripped.token)
        assertEquals(original.expiresAt, roundTripped.expiresAt)
        assertEquals(original.createdAt, roundTripped.createdAt)
    }

    @Test
    fun `should trim email when mapping`() {
        val entity = RefreshTokenJpaEntity(
            id = UUID.randomUUID(),
            email = "  test@example.com  ",
            token = "valid.jwt.token",
            expiresAt = Instant.EPOCH,
            createdAt = Instant.EPOCH
        )

        val domain = refreshTokenMapper.toDomain(entity)

        assertEquals("test@example.com", domain.email.value)
    }

    @Test
    fun `should throw InvalidAuthEmailException when email is invalid`() {
        val entity = RefreshTokenJpaEntity(
            id = UUID.randomUUID(),
            email = "invalid@@example..com",
            token = "valid.jwt.token",
            expiresAt = Instant.EPOCH,
            createdAt = Instant.EPOCH
        )

        assertFailsWith<InvalidAuthEmailException> {
            refreshTokenMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email is blank`() {
        val entity = RefreshTokenJpaEntity(
            id = UUID.randomUUID(),
            email = "   ",
            token = "valid.jwt.token",
            expiresAt = Instant.EPOCH,
            createdAt = Instant.EPOCH
        )

        assertFailsWith<InvalidAuthEmailException> {
            refreshTokenMapper.toDomain(entity)
        }
    }
}
