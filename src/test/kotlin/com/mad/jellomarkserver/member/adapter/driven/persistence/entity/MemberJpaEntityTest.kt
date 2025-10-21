package com.mad.jellomarkserver.member.adapter.driven.persistence.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class MemberJpaEntityTest {

    @Test
    fun `should create MemberJpaEntity with valid values`() {
        val id = UUID.randomUUID()
        val nickname = "testuser"
        val email = "test@example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with minimum length nickname`() {
        val id = UUID.randomUUID()
        val nickname = "ab"
        val email = "a@b.co"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with maximum length nickname`() {
        val id = UUID.randomUUID()
        val nickname = "a".repeat(100)
        val email = "test@example.com"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with maximum length email`() {
        val id = UUID.randomUUID()
        val nickname = "testuser"
        val email = "a".repeat(243) + "@example.com"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with special characters in nickname`() {
        val id = UUID.randomUUID()
        val nickname = "user_123-test.name"
        val email = "test@example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with special characters in email`() {
        val id = UUID.randomUUID()
        val nickname = "testuser"
        val email = "test.user+tag@example.co.uk"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with Korean characters in nickname`() {
        val id = UUID.randomUUID()
        val nickname = "한글닉네임"
        val email = "test@example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with epoch timestamps`() {
        val id = UUID.randomUUID()
        val nickname = "testuser"
        val email = "test@example.com"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with far future timestamps`() {
        val id = UUID.randomUUID()
        val nickname = "testuser"
        val email = "test@example.com"
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with different created and updated timestamps`() {
        val id = UUID.randomUUID()
        val nickname = "testuser"
        val email = "test@example.com"
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-12-31T23:59:59Z")

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should update nickname field`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "oldnickname",
            email = "test@example.com",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newNickname = "newnickname"
        entity.nickname = newNickname

        assertEquals(newNickname, entity.nickname)
    }

    @Test
    fun `should update email field`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "testuser",
            email = "old@example.com",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newEmail = "new@example.com"
        entity.email = newEmail

        assertEquals(newEmail, entity.email)
    }

    @Test
    fun `should update updatedAt field`() {
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val initialUpdatedAt = Instant.parse("2024-01-01T00:00:00Z")

        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "testuser",
            email = "test@example.com",
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
        val entity = MemberJpaEntity(
            id = oldId,
            nickname = "testuser",
            email = "test@example.com",
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
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "testuser",
            email = "test@example.com",
            createdAt = initialCreatedAt,
            updatedAt = Instant.now()
        )

        val newCreatedAt = Instant.parse("2024-06-01T00:00:00Z")
        entity.createdAt = newCreatedAt

        assertEquals(newCreatedAt, entity.createdAt)
    }

    @Test
    fun `should create MemberJpaEntity with numeric nickname`() {
        val id = UUID.randomUUID()
        val nickname = "12345678"
        val email = "test@example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with mixed case nickname`() {
        val id = UUID.randomUUID()
        val nickname = "TestUser123"
        val email = "test@example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with uppercase nickname`() {
        val id = UUID.randomUUID()
        val nickname = "TESTUSER"
        val email = "test@example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with subdomain email`() {
        val id = UUID.randomUUID()
        val nickname = "testuser"
        val email = "user@mail.example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with all zeros UUID`() {
        val id = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val nickname = "testuser"
        val email = "test@example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create multiple MemberJpaEntity instances with different values`() {
        val entity1 = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "user1",
            email = "user1@example.com",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val entity2 = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "user2",
            email = "user2@example.com",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertEquals("user1", entity1.nickname)
        assertEquals("user1@example.com", entity1.email)
        assertEquals("user2", entity2.nickname)
        assertEquals("user2@example.com", entity2.email)
    }

    @Test
    fun `should update all mutable fields`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "oldnickname",
            email = "old@example.com",
            createdAt = Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2024-01-01T00:00:00Z")
        )

        val newId = UUID.randomUUID()
        val newNickname = "newnickname"
        val newEmail = "new@example.com"
        val newCreatedAt = Instant.parse("2024-06-01T00:00:00Z")
        val newUpdatedAt = Instant.parse("2024-12-31T23:59:59Z")

        entity.id = newId
        entity.nickname = newNickname
        entity.email = newEmail
        entity.createdAt = newCreatedAt
        entity.updatedAt = newUpdatedAt

        assertEquals(newId, entity.id)
        assertEquals(newNickname, entity.nickname)
        assertEquals(newEmail, entity.email)
        assertEquals(newCreatedAt, entity.createdAt)
        assertEquals(newUpdatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with long email containing subdomains`() {
        val id = UUID.randomUUID()
        val nickname = "testuser"
        val email = "user@mail.server.subdomain.example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with emoji in nickname`() {
        val id = UUID.randomUUID()
        val nickname = "user😀"
        val email = "test@example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with whitespace in nickname`() {
        val id = UUID.randomUUID()
        val nickname = "user name"
        val email = "test@example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with timestamp precision`() {
        val id = UUID.randomUUID()
        val nickname = "testuser"
        val email = "test@example.com"
        val createdAt = Instant.parse("2024-06-15T10:30:45.123456789Z")
        val updatedAt = Instant.parse("2024-06-15T10:30:45.987654321Z")

        val entity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(nickname, entity.nickname)
        assertEquals(email, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }
}
