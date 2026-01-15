package com.mad.jellomarkserver.member.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.member.adapter.driven.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidSocialIdException
import com.mad.jellomarkserver.member.core.domain.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertFailsWith

class MemberMapperImplTest {

    private val memberMapper = MemberMapperImpl()

    @Test
    fun `should correctly map MemberJpaEntity to Member`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "Nick123"
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val memberJpaEntity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = memberMapper.toDomain(memberJpaEntity)

        assertEquals(MemberId.from(id), result.id)
        assertEquals(SocialProvider.KAKAO, result.socialProvider)
        assertEquals(SocialId(socialId), result.socialId)
        assertEquals(MemberNickname.of(nickname), result.memberNickname)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map MemberJpaEntity with edge case values`() {
        val id = UUID.randomUUID()
        val socialProvider = "NAVER"
        val socialId = "a".repeat(255)
        val nickname = "N".repeat(8)
        val createdAt = Instant.parse("2000-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val memberJpaEntity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = memberMapper.toDomain(memberJpaEntity)

        assertEquals(MemberId.from(id), result.id)
        assertEquals(SocialProvider.NAVER, result.socialProvider)
        assertEquals(SocialId(socialId), result.socialId)
        assertEquals(MemberNickname.of(nickname), result.memberNickname)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map MemberJpaEntity with minimum valid values`() {
        val id = UUID.randomUUID()
        val socialProvider = "GOOGLE"
        val socialId = "1"
        val nickname = "Aa"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val memberJpaEntity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = memberMapper.toDomain(memberJpaEntity)

        assertEquals(MemberId.from(id), result.id)
        assertEquals(SocialProvider.GOOGLE, result.socialProvider)
        assertEquals(SocialId(socialId), result.socialId)
        assertEquals(MemberNickname.of(nickname), result.memberNickname)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map Member to MemberJpaEntity`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123456789")
        val memberNickname = MemberNickname.of("Nick123")
        val displayName = MemberDisplayName.of("테스트유저")
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-06-01T00:00:00Z")

        val domain = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = memberMapper.toEntity(domain)

        assertEquals(id.value, entity.id)
        assertEquals(socialProvider.name, entity.socialProvider)
        assertEquals(socialId.value, entity.socialId)
        assertEquals(memberNickname.value, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `domain - entity - domain round-trip should keep values`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("kakao-user-123")
        val memberNickname = MemberNickname.of("Aa")
        val displayName = MemberDisplayName.of("테스트유저")
        val createdAt = Instant.parse("2020-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2021-01-01T00:00:00Z")
        val original =
            Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = memberMapper.toEntity(original)
        val roundTripped = memberMapper.toDomain(entity)

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.socialProvider, roundTripped.socialProvider)
        assertEquals(original.socialId, roundTripped.socialId)
        assertEquals(original.memberNickname, roundTripped.memberNickname)
        assertEquals(original.createdAt, roundTripped.createdAt)
        assertEquals(original.updatedAt, roundTripped.updatedAt)
    }

    @Test
    fun `should trim nickname when mapping`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "123456789",
            nickname = "  Nick12  ",
            displayName = "테스트유저",
            createdAt = Instant.parse("2020-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2020-01-02T00:00:00Z")
        )

        val domain = memberMapper.toDomain(entity)

        assertEquals("Nick12", domain.memberNickname.value)
    }

    @Test
    fun `should throw when nickname is invalid`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "123456789",
            nickname = "a",
            displayName = "테스트유저",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberNicknameException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw when socialId is blank`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "   ",
            nickname = "testuser",
            displayName = "테스트유저",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidSocialIdException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidMemberNicknameException when nickname is too short`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "123456789",
            nickname = "a",
            displayName = "테스트유저",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberNicknameException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidMemberNicknameException when nickname is too long`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "123456789",
            nickname = "123456789012345678901",
            displayName = "테스트유저",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberNicknameException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidMemberNicknameException when nickname contains only whitespace`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "123456789",
            nickname = "   ",
            displayName = "테스트유저",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberNicknameException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should map all social providers correctly`() {
        val providers = listOf("KAKAO", "NAVER", "GOOGLE")
        val expectedProviders = listOf(SocialProvider.KAKAO, SocialProvider.NAVER, SocialProvider.GOOGLE)

        for ((index, providerName) in providers.withIndex()) {
            val entity = MemberJpaEntity(
                id = UUID.randomUUID(),
                socialProvider = providerName,
                socialId = "user-$index",
                nickname = "testuser",
                displayName = "테스트유저",
                createdAt = Instant.EPOCH,
                updatedAt = Instant.EPOCH
            )

            val domain = memberMapper.toDomain(entity)

            assertEquals(expectedProviders[index], domain.socialProvider)
        }
    }
}
