package com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.AuthJpaEntity
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidAuthEmailException
import com.mad.jellomarkserver.auth.core.domain.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertFailsWith

class AuthMapperImplTest {

    private val authMapper = AuthMapperImpl()

    @Test
    fun `should correctly map AuthJpaEntity to Auth`() {
        val id = UUID.randomUUID()
        val email = "test@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val authJpaEntity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = authMapper.toDomain(authJpaEntity)

        assertEquals(AuthId.from(id), result.id)
        assertEquals(AuthEmail.of(email), result.email)
        assertEquals(HashedPassword.from(hashedPassword), result.hashedPassword)
        assertEquals(UserType.MEMBER, result.userType)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map AuthJpaEntity with OWNER user type`() {
        val id = UUID.randomUUID()
        val email = "owner@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "OWNER"
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val authJpaEntity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = authMapper.toDomain(authJpaEntity)

        assertEquals(AuthId.from(id), result.id)
        assertEquals(AuthEmail.of(email), result.email)
        assertEquals(HashedPassword.from(hashedPassword), result.hashedPassword)
        assertEquals(UserType.OWNER, result.userType)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map AuthJpaEntity with minimum valid email`() {
        val id = UUID.randomUUID()
        val email = "a@b.co"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val authJpaEntity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = authMapper.toDomain(authJpaEntity)

        assertEquals(AuthId.from(id), result.id)
        assertEquals(AuthEmail.of(email), result.email)
        assertEquals(HashedPassword.from(hashedPassword), result.hashedPassword)
        assertEquals(UserType.MEMBER, result.userType)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map Auth to AuthJpaEntity`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-06-01T00:00:00Z")

        val domain = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = authMapper.toEntity(domain)

        assertEquals(id.value, entity.id)
        assertEquals(email.value, entity.email)
        assertEquals(hashedPassword.value, entity.hashedPassword)
        assertEquals("MEMBER", entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should correctly map Auth with OWNER to AuthJpaEntity`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("owner@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.OWNER
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-06-01T00:00:00Z")

        val domain = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = authMapper.toEntity(domain)

        assertEquals(id.value, entity.id)
        assertEquals(email.value, entity.email)
        assertEquals(hashedPassword.value, entity.hashedPassword)
        assertEquals("OWNER", entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `domain - entity - domain round-trip should keep values`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.parse("2020-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2021-01-01T00:00:00Z")
        val original = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = authMapper.toEntity(original)
        val roundTripped = authMapper.toDomain(entity)

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.email, roundTripped.email)
        assertEquals(original.hashedPassword, roundTripped.hashedPassword)
        assertEquals(original.userType, roundTripped.userType)
        assertEquals(original.createdAt, roundTripped.createdAt)
        assertEquals(original.updatedAt, roundTripped.updatedAt)
    }

    @Test
    fun `should trim email when mapping`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "  test@example.com  ",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.parse("2020-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2020-01-02T00:00:00Z")
        )

        val domain = authMapper.toDomain(entity)

        assertEquals("test@example.com", domain.email.value)
    }

    @Test
    fun `should throw InvalidAuthEmailException when email is invalid`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "invalid@@example..com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidAuthEmailException> {
            authMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email is too short`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "a@b.c",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidAuthEmailException> {
            authMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email has no TLD`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "user@example",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidAuthEmailException> {
            authMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email is missing @ symbol`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "userexample.com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidAuthEmailException> {
            authMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email is missing domain`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "user@",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidAuthEmailException> {
            authMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email is blank`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "   ",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidAuthEmailException> {
            authMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email contains multiple @ symbols`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "user@@example.com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidAuthEmailException> {
            authMapper.toDomain(entity)
        }
    }
}
