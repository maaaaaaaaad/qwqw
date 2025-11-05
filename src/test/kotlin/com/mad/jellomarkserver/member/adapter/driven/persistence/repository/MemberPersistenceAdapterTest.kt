package com.mad.jellomarkserver.member.adapter.driven.persistence.repository

import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslatorImpl
import com.mad.jellomarkserver.member.adapter.driven.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.adapter.driven.persistence.mapper.MemberMapper
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.model.MemberEmail
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.MemberNickname
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import java.time.Instant
import java.util.UUID
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class MemberPersistenceAdapterTest {

    @Mock
    private lateinit var jpaRepository: MemberJpaRepository

    @Mock
    private lateinit var mapper: MemberMapper

    private val constraintTranslator: ConstraintViolationTranslator = ConstraintViolationTranslatorImpl()

    private lateinit var adapter: MemberPersistenceAdapter

    @org.junit.jupiter.api.BeforeEach
    fun setup() {
        adapter = MemberPersistenceAdapter(jpaRepository, mapper, constraintTranslator)
    }

    @Test
    fun `should save member successfully`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
        verify(mapper).toEntity(member)
        verify(jpaRepository).saveAndFlush(entity)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should save member with minimum valid values`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("ab")
        val memberEmail = MemberEmail.of("a@a.co")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with maximum length values`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("12345678")
        val memberEmail = MemberEmail.of("a".repeat(243) + "@example.com")
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with special characters in nickname`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("user_123")
        val memberEmail = MemberEmail.of("user@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with special characters in email`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("user+tag@mail.example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with Korean nickname`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("한글닉네임")
        val memberEmail = MemberEmail.of("user@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with different created and updated timestamps`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-06-01T12:30:45Z")
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }


    @Test
    fun `should correctly handle round-trip with mapper`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("rtripmap")
        val memberEmail = MemberEmail.of("rtripmap@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val originalMember = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val reconstructedMember = Member.reconstruct(
            MemberId.from(entity.id),
            MemberNickname.of(entity.nickname),
            MemberEmail.of(entity.email),
            entity.createdAt,
            entity.updatedAt
        )

        `when`(mapper.toEntity(originalMember)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(reconstructedMember)

        val result = adapter.save(originalMember)

        assertEquals(originalMember.id, result.id)
        assertEquals(originalMember.memberNickname, result.memberNickname)
        assertEquals(originalMember.memberEmail, result.memberEmail)
        assertEquals(originalMember.createdAt, result.createdAt)
        assertEquals(originalMember.updatedAt, result.updatedAt)
    }

    @Test
    fun `should save multiple members with different values`() {
        val member1 = Member.reconstruct(
            MemberId.from(UUID.randomUUID()),
            MemberNickname.of("user1"),
            MemberEmail.of("user1@example.com"),
            Instant.parse("2025-01-01T00:00:00Z"),
            Instant.parse("2025-01-01T00:00:00Z")
        )

        val member2 = Member.reconstruct(
            MemberId.from(UUID.randomUUID()),
            MemberNickname.of("user2"),
            MemberEmail.of("user2@example.com"),
            Instant.parse("2025-02-01T00:00:00Z"),
            Instant.parse("2025-02-01T00:00:00Z")
        )

        val entity1 = MemberJpaEntity(
            id = member1.id.value,
            nickname = member1.memberNickname.value,
            email = member1.memberEmail.value,
            createdAt = member1.createdAt,
            updatedAt = member1.updatedAt
        )

        val entity2 = MemberJpaEntity(
            id = member2.id.value,
            nickname = member2.memberNickname.value,
            email = member2.memberEmail.value,
            createdAt = member2.createdAt,
            updatedAt = member2.updatedAt
        )

        `when`(mapper.toEntity(member1)).thenReturn(entity1)
        `when`(jpaRepository.saveAndFlush(entity1)).thenReturn(entity1)
        `when`(mapper.toDomain(entity1)).thenReturn(member1)

        `when`(mapper.toEntity(member2)).thenReturn(entity2)
        `when`(jpaRepository.saveAndFlush(entity2)).thenReturn(entity2)
        `when`(mapper.toDomain(entity2)).thenReturn(member2)

        val result1 = adapter.save(member1)
        val result2 = adapter.save(member2)

        assertEquals(member1, result1)
        assertEquals(member2, result2)
    }

    @Test
    fun `should save member with uppercase email`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("TEST@EXAMPLE.COM")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with numeric nickname`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("12345678")
        val memberEmail = MemberEmail.of("numeric@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with subdomain email`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("user@mail.example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with all zeros UUID`() {
        val id = MemberId.from(UUID(0, 0))
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with high precision timestamp`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T12:34:56.123456789Z")
        val updatedAt = Instant.parse("2025-01-01T12:34:56.987654321Z")
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should throw DuplicateEmailException when email constraint is violated`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("duplicate@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_email")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateMemberEmailException> {
            adapter.save(member)
        }

        assertEquals("Email already in use: duplicate@example.com", thrownException.message)
        verify(mapper).toEntity(member)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should throw DuplicateNicknameException when nickname constraint is violated`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("dupname")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_nickname")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateMemberNicknameException> {
            adapter.save(member)
        }

        assertEquals("Nickname already in use: dupname", thrownException.message)
        verify(mapper).toEntity(member)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should throw DuplicateEmailException with correct email value`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("user123")
        val memberEmail = MemberEmail.of("admin@test.com")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_email")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateMemberEmailException> {
            adapter.save(member)
        }

        assertEquals("Email already in use: admin@test.com", thrownException.message)
    }

    @Test
    fun `should throw DuplicateNicknameException with correct nickname value`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("admin123")
        val memberEmail = MemberEmail.of("user@test.com")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_nickname")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateMemberNicknameException> {
            adapter.save(member)
        }

        assertEquals("Nickname already in use: admin123", thrownException.message)
    }

    @Test
    fun `should throw DuplicateEmailException for email with special characters`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("user+tag@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_email")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateMemberEmailException> {
            adapter.save(member)
        }

        assertEquals("Email already in use: user+tag@example.com", thrownException.message)
    }

    @Test
    fun `should throw DuplicateNicknameException for nickname with special characters`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("user_123")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_nickname")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateMemberNicknameException> {
            adapter.save(member)
        }

        assertEquals("Nickname already in use: user_123", thrownException.message)
    }

    @Test
    fun `should call constraintTranslator when DataIntegrityViolationException occurs`() {
        val id = MemberId.from(UUID.randomUUID())
        val memberNickname = MemberNickname.of("testuser")
        val memberEmail = MemberEmail.of("test@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, memberNickname, memberEmail, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = memberNickname.value,
            email = memberEmail.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_email")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        assertFailsWith<DuplicateMemberEmailException> {
            adapter.save(member)
        }
    }
}
