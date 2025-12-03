package com.mad.jellomarkserver.auth.adapter.driven.persistence.repository

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.AuthJpaEntity
import com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper.AuthMapper
import com.mad.jellomarkserver.auth.core.domain.exception.DuplicateAuthEmailException
import com.mad.jellomarkserver.auth.core.domain.model.*
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslatorImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import java.time.Instant
import java.util.*
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class AuthPersistenceAdapterTest {

    @Mock
    private lateinit var jpaRepository: AuthJpaRepository

    @Mock
    private lateinit var mapper: AuthMapper

    private val constraintTranslator: ConstraintViolationTranslator = ConstraintViolationTranslatorImpl()

    private lateinit var adapter: AuthPersistenceAdapter

    @org.junit.jupiter.api.BeforeEach
    fun setup() {
        adapter = AuthPersistenceAdapter(jpaRepository, mapper, constraintTranslator)
    }

    @Test
    fun `should save auth successfully`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
        verify(mapper).toEntity(auth)
        verify(jpaRepository).saveAndFlush(entity)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should save auth with OWNER user type`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("owner@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.OWNER
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
    }

    @Test
    fun `should save auth with minimum valid email`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("a@b.co")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
    }

    @Test
    fun `should save auth with maximum length email`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("a".repeat(243) + "@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.OWNER
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
    }

    @Test
    fun `should save auth with special characters in email`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("user+tag@mail.example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
    }

    @Test
    fun `should save auth with different created and updated timestamps`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-06-01T12:30:45Z")
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
    }

    @Test
    fun `should correctly handle round-trip with mapper`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("rtripmap@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val originalAuth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val reconstructedAuth = Auth.reconstruct(
            AuthId.from(entity.id),
            AuthEmail.of(entity.email),
            HashedPassword.from(entity.hashedPassword),
            UserType.valueOf(entity.userType),
            entity.createdAt,
            entity.updatedAt
        )

        `when`(mapper.toEntity(originalAuth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(reconstructedAuth)

        val result = adapter.save(originalAuth)

        assertEquals(originalAuth.id, result.id)
        assertEquals(originalAuth.email, result.email)
        assertEquals(originalAuth.hashedPassword, result.hashedPassword)
        assertEquals(originalAuth.userType, result.userType)
        assertEquals(originalAuth.createdAt, result.createdAt)
        assertEquals(originalAuth.updatedAt, result.updatedAt)
    }

    @Test
    fun `should save multiple auths with different values`() {
        val auth1 = Auth.reconstruct(
            AuthId.from(UUID.randomUUID()),
            AuthEmail.of("user1@example.com"),
            HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"),
            UserType.MEMBER,
            Instant.parse("2025-01-01T00:00:00Z"),
            Instant.parse("2025-01-01T00:00:00Z")
        )

        val auth2 = Auth.reconstruct(
            AuthId.from(UUID.randomUUID()),
            AuthEmail.of("user2@example.com"),
            HashedPassword.from("\$2a\$10\$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0"),
            UserType.OWNER,
            Instant.parse("2025-02-01T00:00:00Z"),
            Instant.parse("2025-02-01T00:00:00Z")
        )

        val entity1 = AuthJpaEntity(
            id = auth1.id.value,
            email = auth1.email.value,
            hashedPassword = auth1.hashedPassword.value,
            userType = auth1.userType.name,
            createdAt = auth1.createdAt,
            updatedAt = auth1.updatedAt
        )

        val entity2 = AuthJpaEntity(
            id = auth2.id.value,
            email = auth2.email.value,
            hashedPassword = auth2.hashedPassword.value,
            userType = auth2.userType.name,
            createdAt = auth2.createdAt,
            updatedAt = auth2.updatedAt
        )

        `when`(mapper.toEntity(auth1)).thenReturn(entity1)
        `when`(jpaRepository.saveAndFlush(entity1)).thenReturn(entity1)
        `when`(mapper.toDomain(entity1)).thenReturn(auth1)

        `when`(mapper.toEntity(auth2)).thenReturn(entity2)
        `when`(jpaRepository.saveAndFlush(entity2)).thenReturn(entity2)
        `when`(mapper.toDomain(entity2)).thenReturn(auth2)

        val result1 = adapter.save(auth1)
        val result2 = adapter.save(auth2)

        assertEquals(auth1, result1)
        assertEquals(auth2, result2)
    }

    @Test
    fun `should save auth with uppercase email`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("TEST@EXAMPLE.COM")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
    }

    @Test
    fun `should save auth with subdomain email`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("user@mail.example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
    }

    @Test
    fun `should save auth with all zeros UUID`() {
        val id = AuthId.from(UUID(0, 0))
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
    }

    @Test
    fun `should save auth with high precision timestamp`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.parse("2025-01-01T12:34:56.123456789Z")
        val updatedAt = Instant.parse("2025-01-01T12:34:56.987654321Z")
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
    }

    @Test
    fun `should save auth with numeric email prefix`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("12345@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(auth)

        val result = adapter.save(auth)

        assertEquals(auth, result)
    }

    @Test
    fun `should throw DuplicateAuthEmailException when email constraint is violated`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("duplicate@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_auths_email")

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateAuthEmailException> {
            adapter.save(auth)
        }

        assertEquals("Email already in use: duplicate@example.com", thrownException.message)
        verify(mapper).toEntity(auth)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should throw DuplicateAuthEmailException with correct email value`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("admin@test.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_auths_email")

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateAuthEmailException> {
            adapter.save(auth)
        }

        assertEquals("Email already in use: admin@test.com", thrownException.message)
    }

    @Test
    fun `should throw DuplicateAuthEmailException for email with special characters`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("user+tag@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_auths_email")

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateAuthEmailException> {
            adapter.save(auth)
        }

        assertEquals("Email already in use: user+tag@example.com", thrownException.message)
    }

    @Test
    fun `should throw DuplicateAuthEmailException for OWNER user type`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("owner@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.OWNER
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_auths_email")

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateAuthEmailException> {
            adapter.save(auth)
        }

        assertEquals("Email already in use: owner@example.com", thrownException.message)
    }

    @Test
    fun `should throw DuplicateAuthEmailException for uppercase email`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("ADMIN@EXAMPLE.COM")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_auths_email")

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateAuthEmailException> {
            adapter.save(auth)
        }

        assertEquals("Email already in use: ADMIN@EXAMPLE.COM", thrownException.message)
    }

    @Test
    fun `should call constraintTranslator when DataIntegrityViolationException occurs`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.from("\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        val userType = UserType.MEMBER
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val auth = Auth.reconstruct(id, email, hashedPassword, userType, createdAt, updatedAt)

        val entity = AuthJpaEntity(
            id = id.value,
            email = email.value,
            hashedPassword = hashedPassword.value,
            userType = userType.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_auths_email")

        `when`(mapper.toEntity(auth)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        assertFailsWith<DuplicateAuthEmailException> {
            adapter.save(auth)
        }
    }
}
