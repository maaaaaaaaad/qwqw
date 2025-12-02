package com.mad.jellomarkserver.auth.adapter.driven.persistence.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class AuthJpaEntityTest {

    @Test
    fun `should create AuthJpaEntity with all fields`() {
        val id = UUID.randomUUID()
        val email = "test@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with minimum length email`() {
        val id = UUID.randomUUID()
        val email = "a@b.co"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with maximum length email`() {
        val id = UUID.randomUUID()
        val email = "a".repeat(243) + "@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "OWNER"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with BCrypt hashed password (60 chars)`() {
        val id = UUID.randomUUID()
        val email = "test@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(60, entity.hashedPassword.length)
        assertEquals(hashedPassword, entity.hashedPassword)
    }

    @Test
    fun `should create AuthJpaEntity with MEMBER user type`() {
        val id = UUID.randomUUID()
        val email = "member@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals("MEMBER", entity.userType)
    }

    @Test
    fun `should create AuthJpaEntity with OWNER user type`() {
        val id = UUID.randomUUID()
        val email = "owner@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "OWNER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals("OWNER", entity.userType)
    }

    @Test
    fun `should create AuthJpaEntity with special characters in email`() {
        val id = UUID.randomUUID()
        val email = "test.user+tag@example.co.uk"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with epoch timestamps`() {
        val id = UUID.randomUUID()
        val email = "test@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with far future timestamps`() {
        val id = UUID.randomUUID()
        val email = "test@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with different created and updated timestamps`() {
        val id = UUID.randomUUID()
        val email = "test@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-12-31T23:59:59Z")

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should update email field`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "old@example.com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newEmail = "new@example.com"
        entity.email = newEmail

        assertEquals(newEmail, entity.email)
    }

    @Test
    fun `should update hashedPassword field`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "test@example.com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newHashedPassword = "\$2a\$10\$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012"
        entity.hashedPassword = newHashedPassword

        assertEquals(newHashedPassword, entity.hashedPassword)
    }

    @Test
    fun `should update userType field`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "test@example.com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newUserType = "OWNER"
        entity.userType = newUserType

        assertEquals(newUserType, entity.userType)
    }

    @Test
    fun `should update updatedAt field`() {
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val initialUpdatedAt = Instant.parse("2024-01-01T00:00:00Z")

        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "test@example.com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = createdAt,
            updatedAt = initialUpdatedAt
        )

        val newUpdatedAt = Instant.parse("2024-12-31T23:59:59Z")
        entity.updatedAt = newUpdatedAt

        assertEquals(createdAt, entity.createdAt)
        assertEquals(newUpdatedAt, entity.updatedAt)
    }

    @Test
    fun `should update id field`() {
        val oldId = UUID.randomUUID()
        val entity = AuthJpaEntity(
            id = oldId,
            email = "test@example.com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newId = UUID.randomUUID()
        entity.id = newId

        assertEquals(newId, entity.id)
    }

    @Test
    fun `should update createdAt field`() {
        val initialCreatedAt = Instant.parse("2024-01-01T00:00:00Z")
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "test@example.com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = initialCreatedAt,
            updatedAt = Instant.now()
        )

        val newCreatedAt = Instant.parse("2024-06-01T00:00:00Z")
        entity.createdAt = newCreatedAt

        assertEquals(newCreatedAt, entity.createdAt)
    }

    @Test
    fun `should create AuthJpaEntity with subdomain email`() {
        val id = UUID.randomUUID()
        val email = "user@mail.example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with all zeros UUID`() {
        val id = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val email = "test@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create multiple AuthJpaEntity instances with different values`() {
        val entity1 = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "user1@example.com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val entity2 = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "user2@example.com",
            hashedPassword = "\$2a\$10\$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012",
            userType = "OWNER",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertEquals("user1@example.com", entity1.email)
        assertEquals("MEMBER", entity1.userType)
        assertEquals("user2@example.com", entity2.email)
        assertEquals("OWNER", entity2.userType)
    }

    @Test
    fun `should update all mutable fields`() {
        val entity = AuthJpaEntity(
            id = UUID.randomUUID(),
            email = "old@example.com",
            hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            userType = "MEMBER",
            createdAt = Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2024-01-01T00:00:00Z")
        )

        val newId = UUID.randomUUID()
        val newEmail = "new@example.com"
        val newHashedPassword = "\$2a\$10\$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012"
        val newUserType = "OWNER"
        val newCreatedAt = Instant.parse("2024-06-01T00:00:00Z")
        val newUpdatedAt = Instant.parse("2024-12-31T23:59:59Z")

        entity.id = newId
        entity.email = newEmail
        entity.hashedPassword = newHashedPassword
        entity.userType = newUserType
        entity.createdAt = newCreatedAt
        entity.updatedAt = newUpdatedAt

        assertEquals(newId, entity.id)
        assertEquals(newEmail, entity.email)
        assertEquals(newHashedPassword, entity.hashedPassword)
        assertEquals(newUserType, entity.userType)
        assertEquals(newCreatedAt, entity.createdAt)
        assertEquals(newUpdatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with long email containing subdomains`() {
        val id = UUID.randomUUID()
        val email = "user@mail.server.subdomain.example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with timestamp precision`() {
        val id = UUID.randomUUID()
        val email = "test@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.parse("2024-06-15T10:30:45.123456789Z")
        val updatedAt = Instant.parse("2024-06-15T10:30:45.987654321Z")

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with mixed case email`() {
        val id = UUID.randomUUID()
        val email = "Test.User@Example.COM"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with uppercase email`() {
        val id = UUID.randomUUID()
        val email = "TESTUSER@EXAMPLE.COM"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with numeric email prefix`() {
        val id = UUID.randomUUID()
        val email = "12345@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with dash and underscore in email`() {
        val id = UUID.randomUUID()
        val email = "test_user-123@example.com"
        val hashedPassword = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(email, entity.email)
        assertEquals(hashedPassword, entity.hashedPassword)
        assertEquals(userType, entity.userType)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create AuthJpaEntity with different BCrypt hash variants`() {
        val id = UUID.randomUUID()
        val email = "test@example.com"
        val hashedPassword = "\$2b\$12\$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0"
        val userType = "MEMBER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = AuthJpaEntity(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(60, entity.hashedPassword.length)
        assertEquals(hashedPassword, entity.hashedPassword)
    }
}
