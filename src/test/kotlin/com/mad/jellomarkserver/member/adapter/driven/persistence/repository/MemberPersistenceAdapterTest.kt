package com.mad.jellomarkserver.member.adapter.driven.persistence.repository

import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslatorImpl
import com.mad.jellomarkserver.member.adapter.driven.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.adapter.driven.persistence.mapper.MemberMapper
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateNicknameException
import com.mad.jellomarkserver.member.core.domain.model.Email
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.Nickname
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.stubbing.Answer
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
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("ab")
        val email = Email.of("a@a.co")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("12345678")
        val email = Email.of("a".repeat(243) + "@example.com")
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("user_123")
        val email = Email.of("user@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("testuser")
        val email = Email.of("user+tag@mail.example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("한글닉네임")
        val email = Email.of("user@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-06-01T12:30:45Z")
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("rtripmap")
        val email = Email.of("rtripmap@example.com")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val originalMember = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val reconstructedMember = Member.reconstruct(
            MemberId.from(entity.id),
            Nickname.of(entity.nickname),
            Email.of(entity.email),
            entity.createdAt,
            entity.updatedAt
        )

        `when`(mapper.toEntity(originalMember)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(reconstructedMember)

        val result = adapter.save(originalMember)

        assertEquals(originalMember.id, result.id)
        assertEquals(originalMember.nickname, result.nickname)
        assertEquals(originalMember.email, result.email)
        assertEquals(originalMember.createdAt, result.createdAt)
        assertEquals(originalMember.updatedAt, result.updatedAt)
    }

    @Test
    fun `should save multiple members with different values`() {
        val member1 = Member.reconstruct(
            MemberId.from(UUID.randomUUID()),
            Nickname.of("user1"),
            Email.of("user1@example.com"),
            Instant.parse("2025-01-01T00:00:00Z"),
            Instant.parse("2025-01-01T00:00:00Z")
        )

        val member2 = Member.reconstruct(
            MemberId.from(UUID.randomUUID()),
            Nickname.of("user2"),
            Email.of("user2@example.com"),
            Instant.parse("2025-02-01T00:00:00Z"),
            Instant.parse("2025-02-01T00:00:00Z")
        )

        val entity1 = MemberJpaEntity(
            id = member1.id.value,
            nickname = member1.nickname.value,
            email = member1.email.value,
            createdAt = member1.createdAt,
            updatedAt = member1.updatedAt
        )

        val entity2 = MemberJpaEntity(
            id = member2.id.value,
            nickname = member2.nickname.value,
            email = member2.email.value,
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
        val nickname = Nickname.of("testuser")
        val email = Email.of("TEST@EXAMPLE.COM")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("12345678")
        val email = Email.of("numeric@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("testuser")
        val email = Email.of("user@mail.example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.parse("2025-01-01T12:34:56.123456789Z")
        val updatedAt = Instant.parse("2025-01-01T12:34:56.987654321Z")
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
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
        val nickname = Nickname.of("testuser")
        val email = Email.of("duplicate@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_email")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateEmailException> {
            adapter.save(member)
        }

        assertEquals("Email already in use: duplicate@example.com", thrownException.message)
        verify(mapper).toEntity(member)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should throw DuplicateNicknameException when nickname constraint is violated`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("dupname")
        val email = Email.of("test@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_nickname")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateNicknameException> {
            adapter.save(member)
        }

        assertEquals("Nickname already in use: dupname", thrownException.message)
        verify(mapper).toEntity(member)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should throw DuplicateEmailException with correct email value`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("user123")
        val email = Email.of("admin@test.com")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_email")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateEmailException> {
            adapter.save(member)
        }

        assertEquals("Email already in use: admin@test.com", thrownException.message)
    }

    @Test
    fun `should throw DuplicateNicknameException with correct nickname value`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("admin123")
        val email = Email.of("user@test.com")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_nickname")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateNicknameException> {
            adapter.save(member)
        }

        assertEquals("Nickname already in use: admin123", thrownException.message)
    }

    @Test
    fun `should throw DuplicateEmailException for email with special characters`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("testuser")
        val email = Email.of("user+tag@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_email")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateEmailException> {
            adapter.save(member)
        }

        assertEquals("Email already in use: user+tag@example.com", thrownException.message)
    }

    @Test
    fun `should throw DuplicateNicknameException for nickname with special characters`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("user_123")
        val email = Email.of("test@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_nickname")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateNicknameException> {
            adapter.save(member)
        }

        assertEquals("Nickname already in use: user_123", thrownException.message)
    }

    @Test
    fun `should call constraintTranslator when DataIntegrityViolationException occurs`() {
        val id = MemberId.from(UUID.randomUUID())
        val nickname = Nickname.of("testuser")
        val email = Email.of("test@example.com")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, nickname, email, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            nickname = nickname.value,
            email = email.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_email")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        assertFailsWith<DuplicateEmailException> {
            adapter.save(member)
        }
    }
}
