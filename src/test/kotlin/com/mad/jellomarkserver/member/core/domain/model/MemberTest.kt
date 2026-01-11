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
    fun `should create Member with valid social provider, social id, and nickname`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("1234567890")
        val memberNickname = MemberNickname.of("testuser")

        val member = Member.create(socialProvider, socialId, memberNickname)

        assertNotNull(member.id)
        assertEquals(socialProvider, member.socialProvider)
        assertEquals(socialId, member.socialId)
        assertEquals(memberNickname, member.memberNickname)
        assertNotNull(member.createdAt)
        assertNotNull(member.updatedAt)
        assertEquals(member.createdAt, member.updatedAt)
    }

    @Test
    fun `should create Member with minimum length nickname`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("ab")

        val member = Member.create(socialProvider, socialId, memberNickname)

        assertEquals(memberNickname, member.memberNickname)
    }

    @Test
    fun `should create Member with maximum length nickname`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("12345678")

        val member = Member.create(socialProvider, socialId, memberNickname)

        assertEquals(memberNickname, member.memberNickname)
    }

    @Test
    fun `should create Member with special characters in nickname`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("user_123")

        val member = Member.create(socialProvider, socialId, memberNickname)

        assertEquals(memberNickname, member.memberNickname)
    }

    @Test
    fun `should create Member with Korean nickname`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("한글닉네임")

        val member = Member.create(socialProvider, socialId, memberNickname)

        assertEquals(memberNickname, member.memberNickname)
    }

    @Test
    fun `should create Member with numeric nickname`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("12345678")

        val member = Member.create(socialProvider, socialId, memberNickname)

        assertEquals(memberNickname, member.memberNickname)
    }

    @Test
    fun `should create Member with fixed clock`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")
        val fixedInstant = Instant.parse("2025-01-01T00:00:00Z")
        val fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"))

        val member = Member.create(socialProvider, socialId, memberNickname, fixedClock)

        assertEquals(fixedInstant, member.createdAt)
        assertEquals(fixedInstant, member.updatedAt)
    }

    @Test
    fun `should create Member with system clock by default`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")
        val before = Instant.now()

        val member = Member.create(socialProvider, socialId, memberNickname)

        val after = Instant.now()
        assert(member.createdAt in before..after)
        assert(member.updatedAt in before..after)
    }

    @Test
    fun `should create Member with NAVER social provider`() {
        val socialProvider = SocialProvider.NAVER
        val socialId = SocialId("naver-id-123")
        val memberNickname = MemberNickname.of("naver")

        val member = Member.create(socialProvider, socialId, memberNickname)

        assertEquals(SocialProvider.NAVER, member.socialProvider)
    }

    @Test
    fun `should create Member with GOOGLE social provider`() {
        val socialProvider = SocialProvider.GOOGLE
        val socialId = SocialId("google-id-456")
        val memberNickname = MemberNickname.of("google")

        val member = Member.create(socialProvider, socialId, memberNickname)

        assertEquals(SocialProvider.GOOGLE, member.socialProvider)
    }

    @Test
    fun `should create Member with Kakao Long ID converted to string`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId.fromKakaoId(3456789012345L)
        val memberNickname = MemberNickname.of("kakao")

        val member = Member.create(socialProvider, socialId, memberNickname)

        assertEquals("3456789012345", member.socialId.value)
    }

    @Test
    fun `should reconstruct Member with all fields`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(socialProvider, member.socialProvider)
        assertEquals(socialId, member.socialId)
        assertEquals(memberNickname, member.memberNickname)
        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with minimum valid values`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("1")
        val memberNickname = MemberNickname.of("ab")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(memberNickname, member.memberNickname)
        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with different created and updated timestamps`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-06-01T12:30:45Z")

        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, createdAt, updatedAt)

        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
        assertNotEquals(member.createdAt, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with high precision timestamp`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")
        val createdAt = Instant.parse("2025-01-01T12:34:56.123456789Z")
        val updatedAt = Instant.parse("2025-01-01T12:34:56.987654321Z")

        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, createdAt, updatedAt)

        assertEquals(createdAt, member.createdAt)
        assertEquals(updatedAt, member.updatedAt)
    }

    @Test
    fun `should have equality based on id`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId1 = SocialId("123")
        val socialId2 = SocialId("456")
        val memberNickname1 = MemberNickname.of("user1")
        val memberNickname2 = MemberNickname.of("user2")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id, socialProvider, socialId1, memberNickname1, createdAt, updatedAt)
        val member2 = Member.reconstruct(id, socialProvider, socialId2, memberNickname2, createdAt, updatedAt)

        assertEquals(member1, member2)
    }

    @Test
    fun `should not be equal when ids are different`() {
        val id1 = MemberId.from(UUID.randomUUID())
        val id2 = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id1, socialProvider, socialId, memberNickname, createdAt, updatedAt)
        val member2 = Member.reconstruct(id2, socialProvider, socialId, memberNickname, createdAt, updatedAt)

        assertNotEquals(member1, member2)
    }

    @Test
    fun `should be equal to itself`() {
        val member = Member.create(SocialProvider.KAKAO, SocialId("123"), MemberNickname.of("testuser"))

        assertEquals(member, member)
    }

    @Test
    fun `should not be equal to null`() {
        val member = Member.create(SocialProvider.KAKAO, SocialId("123"), MemberNickname.of("testuser"))

        assertNotEquals(member, null)
    }

    @Test
    fun `should not be equal to different type`() {
        val member = Member.create(SocialProvider.KAKAO, SocialId("123"), MemberNickname.of("testuser"))

        assertNotEquals(member, "string")
    }

    @Test
    fun `should have same hashCode when ids are equal`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId1 = SocialId("123")
        val socialId2 = SocialId("456")
        val memberNickname1 = MemberNickname.of("user1")
        val memberNickname2 = MemberNickname.of("user2")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id, socialProvider, socialId1, memberNickname1, createdAt, updatedAt)
        val member2 = Member.reconstruct(id, socialProvider, socialId2, memberNickname2, createdAt, updatedAt)

        assertEquals(member1.hashCode(), member2.hashCode())
    }

    @Test
    fun `should have different hashCode when ids are different`() {
        val id1 = MemberId.from(UUID.randomUUID())
        val id2 = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member1 = Member.reconstruct(id1, socialProvider, socialId, memberNickname, createdAt, updatedAt)
        val member2 = Member.reconstruct(id2, socialProvider, socialId, memberNickname, createdAt, updatedAt)

        assertNotEquals(member1.hashCode(), member2.hashCode())
    }

    @Test
    fun `should generate unique ids for different members created`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")

        val member1 = Member.create(socialProvider, socialId, memberNickname)
        val member2 = Member.create(socialProvider, socialId, memberNickname)

        assertNotEquals(member1.id, member2.id)
        assertNotEquals(member1, member2)
    }

    @Test
    fun `should create Member with epoch timestamp using fixed clock`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")
        val fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"))

        val member = Member.create(socialProvider, socialId, memberNickname, fixedClock)

        assertEquals(Instant.EPOCH, member.createdAt)
        assertEquals(Instant.EPOCH, member.updatedAt)
    }

    @Test
    fun `should create Member with far future timestamp using fixed clock`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")
        val futureInstant = Instant.parse("2099-12-31T23:59:59Z")
        val fixedClock = Clock.fixed(futureInstant, ZoneId.of("UTC"))

        val member = Member.create(socialProvider, socialId, memberNickname, fixedClock)

        assertEquals(futureInstant, member.createdAt)
        assertEquals(futureInstant, member.updatedAt)
    }

    @Test
    fun `should reconstruct Member with Korean nickname`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("한글닉네임")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, createdAt, updatedAt)

        assertEquals(memberNickname, member.memberNickname)
    }

    @Test
    fun `should create multiple members with different values`() {
        val member1 = Member.create(SocialProvider.KAKAO, SocialId("111"), MemberNickname.of("user1"))
        val member2 = Member.create(SocialProvider.NAVER, SocialId("222"), MemberNickname.of("user2"))

        assertNotEquals(member1.id, member2.id)
        assertNotEquals(member1.socialProvider, member2.socialProvider)
        assertNotEquals(member1.socialId, member2.socialId)
        assertNotEquals(member1.memberNickname, member2.memberNickname)
    }

    @Test
    fun `should maintain id consistency across multiple operations`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123")
        val memberNickname = MemberNickname.of("testuser")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, createdAt, updatedAt)

        assertEquals(id, member.id)
        assertEquals(id.hashCode(), member.id.hashCode())
    }
}
