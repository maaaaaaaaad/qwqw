package com.mad.jellomarkserver.member.core.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

class MemberTest {

    @Test
    fun `should create Member with valid nickname and email`() {
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")

        val member = Member.create(nickname, email)

        assertNotNull(member.id)
        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
        assertNotNull(member.createdAt)
        assertNotNull(member.updatedAt)
        assertEquals(member.createdAt, member.updatedAt)
    }

    @Test
    fun `should create Member with minimum length nickname`() {
        val nickname = Nickname.of("ab")
        val email = Email.of("a@a.co")

        val member = Member.create(nickname, email)

        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
    }

    @Test
    fun `should create Member with maximum length nickname`() {
        val nickname = Nickname.of("12345678")
        val email = Email.of("test@example.com")

        val member = Member.create(nickname, email)

        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
    }

    @Test
    fun `should create Member with special characters in nickname`() {
        val nickname = Nickname.of("user_123")
        val email = Email.of("test@example.com")

        val member = Member.create(nickname, email)

        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
    }

    @Test
    fun `should create Member with special characters in email`() {
        val nickname = Nickname.of("testuser")
        val email = Email.of("user+tag@example.com")

        val member = Member.create(nickname, email)

        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
    }

    @Test
    fun `should create Member with Korean nickname`() {
        val nickname = Nickname.of("한글닉네임")
        val email = Email.of("test@example.com")

        val member = Member.create(nickname, email)

        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
    }

    @Test
    fun `should create Member with subdomain email`() {
        val nickname = Nickname.of("testuser")
        val email = Email.of("user@mail.example.com")

        val member = Member.create(nickname, email)

        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
    }

    @Test
    fun `should create Member with numeric nickname`() {
        val nickname = Nickname.of("12345678")
        val email = Email.of("numeric@example.com")

        val member = Member.create(nickname, email)

        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
    }

    @Test
    fun `should create Member with uppercase email`() {
        val nickname = Nickname.of("testuser")
        val email = Email.of("TEST@EXAMPLE.COM")

        val member = Member.create(nickname, email)

        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
    }

    @Test
    fun `should create Member with fixed clock`() {
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val fixedInstant = Instant.parse("2025-01-01T00:00:00Z")
        val fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"))

        val member = Member.create(nickname, email, fixedClock)

        assertEquals(fixedInstant, member.createdAt)
        assertEquals(fixedInstant, member.updatedAt)
    }

    @Test
    fun `should create Member with system clock by default`() {
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val before = Instant.now()

        val member = Member.create(nickname, email)

        val after = Instant.now()
        assert(member.createdAt in before..after)
        assert(member.updatedAt in before..after)
    }

    @Test
    fun `should reconstruct Member with all fields`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with minimum valid values`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("ab")
        val email = Email.of("a@a.co")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with maximum length values`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("12345678")
        val email = Email.of("a".repeat(243) + "@example.com")
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")

        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
    }

    @Test
    fun `should reconstruct Member with different created and updated timestamps`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-06-01T12:30:45Z")

        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
        assertNotEquals(member.createdAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with high precision timestamp`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T12:34:56.123456789Z")
        val updatedAt = Instant.parse("2025-01-01T12:34:56.987654321Z")

        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with all zeros UUID`() {
        val id = MemberId.from(UUID(0, 0))
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        assertEquals(id, member.id)
    }

    @Test
    fun `should have equality based on id`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname1 = Nickname.of("user1")
        val nickname2 = Nickname.of("user2")
        val email1 = Email.of("user1@example.com")
        val email2 = Email.of("user2@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id, nickname1, email1, createdAt, updatedAt)
        val member2 = Member.reconstruct(id, nickname2, email2, createdAt, updatedAt)

        assertEquals(member1, member2)
    }

    @Test
    fun `should not be equal when ids are different`() {
        val id1 = MemberId.from(UUID.randomUUID())
        val id2 = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id1, nickname, email, createdAt, updatedAt)
        val member2 = Member.reconstruct(id2, nickname, email, createdAt, updatedAt)

        assertNotEquals(member1, member2)
    }

    @Test
    fun `should be equal to itself`() {
        val member = Member.create(Nickname.of("testuser"), Email.of("test@example.com"))

        assertEquals(member, member)
    }

    @Test
    fun `should not be equal to null`() {
        val member = Member.create(Nickname.of("testuser"), Email.of("test@example.com"))

        assertNotEquals(member, null)
    }

    @Test
    fun `should not be equal to different type`() {
        val member = Member.create(Nickname.of("testuser"), Email.of("test@example.com"))

        assertNotEquals(member, "string")
    }

    @Test
    fun `should have same hashCode when ids are equal`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname1 = Nickname.of("user1")
        val nickname2 = Nickname.of("user2")
        val email1 = Email.of("user1@example.com")
        val email2 = Email.of("user2@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id, nickname1, email1, createdAt, updatedAt)
        val member2 = Member.reconstruct(id, nickname2, email2, createdAt, updatedAt)

        assertEquals(member1.hashCode(), member2.hashCode())
    }

    @Test
    fun `should have different hashCode when ids are different`() {
        val id1 = MemberId.from(UUID.randomUUID())
        val id2 = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id1, nickname, email, createdAt, updatedAt)
        val member2 = Member.reconstruct(id2, nickname, email, createdAt, updatedAt)

        assertNotEquals(member1.hashCode(), member2.hashCode())
    }

    @Test
    fun `should generate unique ids for different members created`() {
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")

        val member1 = Member.create(nickname, email)
        val member2 = Member.create(nickname, email)

        assertNotEquals(member1.id, member2.id)
        assertNotEquals(member1, member2)
    }

    @Test
    fun `should create Member with epoch timestamp using fixed clock`() {
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"))

        val member = Member.create(nickname, email, fixedClock)

        assertEquals(Instant.EPOCH, member.createdAt)
        assertEquals(Instant.EPOCH, member.updatedAt)
    }

    @Test
    fun `should create Member with far future timestamp using fixed clock`() {
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val futureInstant = Instant.parse("2099-12-31T23:59:59Z")
        val fixedClock = Clock.fixed(futureInstant, ZoneId.of("UTC"))

        val member = Member.create(nickname, email, fixedClock)

        assertEquals(futureInstant, member.createdAt)
        assertEquals(futureInstant, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with Korean nickname and special email`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("한글닉네임")
        val email = Email.of("user+tag@mail.example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        assertEquals(nickname, member.nickname)
        assertEquals(email, member.email)
    }

    @Test
    fun `should create multiple members with different values`() {
        val member1 = Member.create(Nickname.of("user1"), Email.of("user1@example.com"))
        val member2 = Member.create(Nickname.of("user2"), Email.of("user2@example.com"))

        assertNotEquals(member1.id, member2.id)
        assertNotEquals(member1.nickname, member2.nickname)
        assertNotEquals(member1.email, member2.email)
    }

    @Test
    fun `should maintain id consistency across multiple operations`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(id.hashCode(), member.id.hashCode())
    }
}
