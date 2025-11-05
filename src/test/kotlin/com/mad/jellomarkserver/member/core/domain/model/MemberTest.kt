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
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")

        val member = Member.create(memberNickname, memberEmail)

        assertNotNull(member.id)
        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
        assertNotNull(member.createdAt)
        assertNotNull(member.updatedAt)
        assertEquals(member.createdAt, member.updatedAt)
    }

    @Test
    fun `should create Member with minimum length nickname`() {
        val memberNickname = MemberNickname.of("ab")
        val memberEmail = MemberEmail.of("a@a.co")

        val member = Member.create(memberNickname, memberEmail)

        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
    }

    @Test
    fun `should create Member with maximum length nickname`() {
        val memberNickname = MemberNickname.of("12345678")
        val memberEmail = MemberEmail.of("test@example.com")

        val member = Member.create(memberNickname, memberEmail)

        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
    }

    @Test
    fun `should create Member with special characters in nickname`() {
        val memberNickname = MemberNickname.of("user_123")
        val memberEmail = MemberEmail.of("test@example.com")

        val member = Member.create(memberNickname, memberEmail)

        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
    }

    @Test
    fun `should create Member with special characters in email`() {
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("user+tag@example.com")

        val member = Member.create(memberNickname, memberEmail)

        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
    }

    @Test
    fun `should create Member with Korean nickname`() {
        val memberNickname = MemberNickname.of("한글닉네임")
        val memberEmail = MemberEmail.of("test@example.com")

        val member = Member.create(memberNickname, memberEmail)

        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
    }

    @Test
    fun `should create Member with subdomain email`() {
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("user@mail.example.com")

        val member = Member.create(memberNickname, memberEmail)

        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
    }

    @Test
    fun `should create Member with numeric nickname`() {
        val memberNickname = MemberNickname.of("12345678")
        val memberEmail = MemberEmail.of("numeric@example.com")

        val member = Member.create(memberNickname, memberEmail)

        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
    }

    @Test
    fun `should create Member with uppercase email`() {
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("TEST@EXAMPLE.COM")

        val member = Member.create(memberNickname, memberEmail)

        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
    }

    @Test
    fun `should create Member with fixed clock`() {
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val fixedInstant = Instant.parse("2025-01-01T00:00:00Z")
        val fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"))

        val member = Member.create(memberNickname, memberEmail, fixedClock)

        assertEquals(fixedInstant, member.createdAt)
        assertEquals(fixedInstant, member.updatedAt)
    }

    @Test
    fun `should create Member with system clock by default`() {
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val before = Instant.now()

        val member = Member.create(memberNickname, memberEmail)

        val after = Instant.now()
        assert(member.createdAt in before..after)
        assert(member.updatedAt in before..after)
    }

    @Test
    fun `should reconstruct Member with all fields`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with minimum valid values`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("ab")
        val memberEmail = MemberEmail.of("a@a.co")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with maximum length values`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("12345678")
        val memberEmail = MemberEmail.of("a".repeat(243) + "@example.com")
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")

        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
    }

    @Test
    fun `should reconstruct Member with different created and updated timestamps`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-06-01T12:30:45Z")

        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
        assertNotEquals(member.createdAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with high precision timestamp`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T12:34:56.123456789Z")
        val updatedAt = Instant.parse("2025-01-01T12:34:56.987654321Z")

        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with all zeros UUID`() {
        val id = MemberId.from(UUID(0, 0))
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        assertEquals(id, member.id)
    }

    @Test
    fun `should have equality based on id`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname1 = MemberNickname.of("user1")
        val memberNickname2 = MemberNickname.of("user2")
        val memberEmail1 = MemberEmail.of("user1@example.com")
        val memberEmail2 = MemberEmail.of("user2@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id, memberNickname1, memberEmail1, createdAt, updatedAt)
        val member2 = Member.reconstruct(id, memberNickname2, memberEmail2, createdAt, updatedAt)

        assertEquals(member1, member2)
    }

    @Test
    fun `should not be equal when ids are different`() {
        val id1 = MemberId.from(UUID.randomUUID())
        val id2 = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id1, memberNickname, memberEmail, createdAt, updatedAt)
        val member2 = Member.reconstruct(id2, memberNickname, memberEmail, createdAt, updatedAt)

        assertNotEquals(member1, member2)
    }

    @Test
    fun `should be equal to itself`() {
        val member = Member.create(MemberNickname.of("testuser"), MemberEmail.of("test@example.com"))

        assertEquals(member, member)
    }

    @Test
    fun `should not be equal to null`() {
        val member = Member.create(MemberNickname.of("testuser"), MemberEmail.of("test@example.com"))

        assertNotEquals(member, null)
    }

    @Test
    fun `should not be equal to different type`() {
        val member = Member.create(MemberNickname.of("testuser"), MemberEmail.of("test@example.com"))

        assertNotEquals(member, "string")
    }

    @Test
    fun `should have same hashCode when ids are equal`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname1 = MemberNickname.of("user1")
        val memberNickname2 = MemberNickname.of("user2")
        val memberEmail1 = MemberEmail.of("user1@example.com")
        val memberEmail2 = MemberEmail.of("user2@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id, memberNickname1, memberEmail1, createdAt, updatedAt)
        val member2 = Member.reconstruct(id, memberNickname2, memberEmail2, createdAt, updatedAt)

        assertEquals(member1.hashCode(), member2.hashCode())
    }

    @Test
    fun `should have different hashCode when ids are different`() {
        val id1 = MemberId.from(UUID.randomUUID())
        val id2 = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id1, memberNickname, memberEmail, createdAt, updatedAt)
        val member2 = Member.reconstruct(id2, memberNickname, memberEmail, createdAt, updatedAt)

        assertNotEquals(member1.hashCode(), member2.hashCode())
    }

    @Test
    fun `should generate unique ids for different members created`() {
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")

        val member1 = Member.create(memberNickname, memberEmail)
        val member2 = Member.create(memberNickname, memberEmail)

        assertNotEquals(member1.id, member2.id)
        assertNotEquals(member1, member2)
    }

    @Test
    fun `should create Member with epoch timestamp using fixed clock`() {
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"))

        val member = Member.create(memberNickname, memberEmail, fixedClock)

        assertEquals(Instant.EPOCH, member.createdAt)
        assertEquals(Instant.EPOCH, member.updatedAt)
    }

    @Test
    fun `should create Member with far future timestamp using fixed clock`() {
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val futureInstant = Instant.parse("2099-12-31T23:59:59Z")
        val fixedClock = Clock.fixed(futureInstant, ZoneId.of("UTC"))

        val member = Member.create(memberNickname, memberEmail, fixedClock)

        assertEquals(futureInstant, member.createdAt)
        assertEquals(futureInstant, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with Korean nickname and special email`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("한글닉네임")
        val memberEmail = MemberEmail.of("user+tag@mail.example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        assertEquals(memberNickname, member.memberNickname)
        assertEquals(memberEmail, member.memberEmail)
    }

    @Test
    fun `should create multiple members with different values`() {
        val member1 = Member.create(MemberNickname.of("user1"), MemberEmail.of("user1@example.com"))
        val member2 = Member.create(MemberNickname.of("user2"), MemberEmail.of("user2@example.com"))

        assertNotEquals(member1.id, member2.id)
        assertNotEquals(member1.memberNickname, member2.memberNickname)
        assertNotEquals(member1.memberEmail, member2.memberEmail)
    }

    @Test
    fun `should maintain id consistency across multiple operations`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(id.hashCode(), member.id.hashCode())
    }
}
