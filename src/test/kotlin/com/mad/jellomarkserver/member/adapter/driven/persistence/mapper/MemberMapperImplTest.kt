package com.mad.jellomarkserver.member.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.member.adapter.driven.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberEmail
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.MemberNickname
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
        val nickname = "Nick123"
        val email = "test@example.com"
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val memberJpaEntity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = memberMapper.toDomain(memberJpaEntity)

        assertEquals(MemberId.from(id), result.id)
        assertEquals(MemberNickname.of(nickname), result.memberNickname)
        assertEquals(MemberEmail.of(email), result.memberEmail)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map MemberJpaEntity with edge case values`() {
        val id = UUID.randomUUID()
        val nickname = "N".repeat(8)
        val email = "e".repeat(243) + "@e.com"
        val createdAt = Instant.parse("2000-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val memberJpaEntity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = memberMapper.toDomain(memberJpaEntity)

        assertEquals(MemberId.from(id), result.id)
        assertEquals(MemberNickname.of(nickname), result.memberNickname)
        assertEquals(MemberEmail.of(email), result.memberEmail)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map MemberJpaEntity with minimum valid values`() {
        val id = UUID.randomUUID()
        val nickname = "Aa"
        val email = "a@a.com"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val memberJpaEntity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = memberMapper.toDomain(memberJpaEntity)

        assertEquals(MemberId.from(id), result.id)
        assertEquals(MemberNickname.of(nickname), result.memberNickname)
        assertEquals(MemberEmail.of(email), result.memberEmail)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map Member to MemberJpaEntity`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("Nick123")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-06-01T00:00:00Z")

        val domain = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = memberMapper.toEntity(domain)

        assertEquals(id.value, entity.id)
        assertEquals(memberNickname.value, entity.nickname)
        assertEquals(memberEmail.value, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `domain - entity - domain round-trip should keep values`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("Aa")
        val memberEmail = MemberEmail.of("a@a.com")
        val createdAt = Instant.parse("2020-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2021-01-01T00:00:00Z")
        val original = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = memberMapper.toEntity(original)
        val roundTripped = memberMapper.toDomain(entity)

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.memberNickname, roundTripped.memberNickname)
        assertEquals(original.memberEmail, roundTripped.memberEmail)
        assertEquals(original.createdAt, roundTripped.createdAt)
        assertEquals(original.updatedAt, roundTripped.updatedAt)
    }

    @Test
    fun `should trim nickname and email when mapping`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "  Nick12  ",
            email = "  test@example.com  ",
            createdAt = Instant.parse("2020-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2020-01-02T00:00:00Z")
        )

        val domain = memberMapper.toDomain(entity)

        assertEquals("Nick12", domain.memberNickname.value)
        assertEquals("test@example.com", domain.memberEmail.value)
    }

    @Test
    fun `should throw when nickname is invalid`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "a",
            email = "test@example.com",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberNicknameException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw when email is invalid`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "Aa",
            email = "invalid@@example..com",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberEmailException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidMemberEmailException when email is too short`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "testuser",
            email = "a@a.c",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberEmailException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidMemberEmailException when email has no TLD`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "testuser",
            email = "user@example",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberEmailException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidMemberEmailException when email is missing @ symbol`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "testuser",
            email = "userexample.com",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberEmailException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidMemberEmailException when email is missing domain`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "testuser",
            email = "user@",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberEmailException> {
            memberMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw InvalidMemberNicknameException when nickname is too short`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            nickname = "a",
            email = "test@example.com",
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
            nickname = "abcdefghi",
            email = "test@example.com",
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
            nickname = "   ",
            email = "test@example.com",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidMemberNicknameException> {
            memberMapper.toDomain(entity)
        }
    }
}