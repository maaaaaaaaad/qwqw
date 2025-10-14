package com.mad.jellomarkserver.member.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.member.adapter.driven.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.core.domain.model.Email
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.Nickname
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class MemberMapperImplTest {

    private val memberMapper = MemberMapperImpl()

    @Test
    fun `should correctly map MemberJpaEntity to Member`() {
        val id = UUID.randomUUID()
        val nickname = "Nick123"
        val email = "test@example.com"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val memberJpaEntity = MemberJpaEntity(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = memberMapper.toDomain(memberJpaEntity)

        assertEquals(MemberId.from(id), result.id)
        assertEquals(Nickname.of(nickname), result.nickname)
        assertEquals(Email.of(email), result.email)
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
        assertEquals(Nickname.of(nickname), result.nickname)
        assertEquals(Email.of(email), result.email)
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
        assertEquals(Nickname.of(nickname), result.nickname)
        assertEquals(Email.of(email), result.email)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map Member to MemberJpaEntity`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("Nick123")
        val email = Email.of("test@example.com")
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-06-01T00:00:00Z")

        val domain = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = memberMapper.toEntity(domain)

        assertEquals(id.value, entity.id)
        assertEquals(nickname.value, entity.nickname)
        assertEquals(email.value, entity.email)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }
}