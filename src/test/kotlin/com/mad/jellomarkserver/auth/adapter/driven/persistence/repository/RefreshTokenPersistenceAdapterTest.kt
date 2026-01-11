package com.mad.jellomarkserver.auth.adapter.driven.persistence.repository

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.RefreshTokenJpaEntity
import com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper.RefreshTokenMapper
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken
import com.mad.jellomarkserver.auth.core.domain.model.RefreshTokenId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class RefreshTokenPersistenceAdapterTest {

    @Mock
    private lateinit var jpaRepository: RefreshTokenJpaRepository

    @Mock
    private lateinit var mapper: RefreshTokenMapper

    private lateinit var adapter: RefreshTokenPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = RefreshTokenPersistenceAdapter(jpaRepository, mapper)
    }

    @Test
    fun `should save refresh token successfully`() {
        val id = RefreshTokenId.from(UUID.randomUUID())
        val identifier = "test@example.com"
        val userType = "OWNER"
        val token = "valid.jwt.refresh.token"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val refreshToken = RefreshToken.reconstruct(id, identifier, userType, token, expiresAt, createdAt)

        val entity = RefreshTokenJpaEntity(
            id = id.value,
            identifier = identifier,
            userType = userType,
            token = token,
            expiresAt = expiresAt,
            createdAt = createdAt
        )

        `when`(mapper.toEntity(refreshToken)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(refreshToken)

        val result = adapter.save(refreshToken)

        assertEquals(refreshToken, result)
        verify(mapper).toEntity(refreshToken)
        verify(jpaRepository).saveAndFlush(entity)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should find refresh token by identifier`() {
        val identifier = "test@example.com"
        val id = RefreshTokenId.from(UUID.randomUUID())
        val userType = "OWNER"
        val token = "valid.jwt.refresh.token"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val refreshToken = RefreshToken.reconstruct(id, identifier, userType, token, expiresAt, createdAt)

        val entity = RefreshTokenJpaEntity(
            id = id.value,
            identifier = identifier,
            userType = userType,
            token = token,
            expiresAt = expiresAt,
            createdAt = createdAt
        )

        `when`(jpaRepository.findByIdentifier(identifier)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(refreshToken)

        val result = adapter.findByIdentifier(identifier)

        assertEquals(refreshToken, result)
        verify(jpaRepository).findByIdentifier(identifier)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should return null when refresh token not found by identifier`() {
        val identifier = "notfound@example.com"

        `when`(jpaRepository.findByIdentifier(identifier)).thenReturn(null)

        val result = adapter.findByIdentifier(identifier)

        assertNull(result)
        verify(jpaRepository).findByIdentifier(identifier)
    }

    @Test
    fun `should find refresh token by token`() {
        val token = "valid.jwt.refresh.token"
        val id = RefreshTokenId.from(UUID.randomUUID())
        val identifier = "test@example.com"
        val userType = "OWNER"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val refreshToken = RefreshToken.reconstruct(id, identifier, userType, token, expiresAt, createdAt)

        val entity = RefreshTokenJpaEntity(
            id = id.value,
            identifier = identifier,
            userType = userType,
            token = token,
            expiresAt = expiresAt,
            createdAt = createdAt
        )

        `when`(jpaRepository.findByToken(token)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(refreshToken)

        val result = adapter.findByToken(token)

        assertEquals(refreshToken, result)
        verify(jpaRepository).findByToken(token)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should return null when refresh token not found by token`() {
        val token = "nonexistent.token"

        `when`(jpaRepository.findByToken(token)).thenReturn(null)

        val result = adapter.findByToken(token)

        assertNull(result)
        verify(jpaRepository).findByToken(token)
    }

    @Test
    fun `should delete refresh token by identifier`() {
        val identifier = "test@example.com"

        adapter.deleteByIdentifier(identifier)

        verify(jpaRepository).deleteByIdentifier(identifier)
    }

    @Test
    fun `should save refresh token with socialId identifier`() {
        val id = RefreshTokenId.from(UUID.randomUUID())
        val identifier = "3456789012345"
        val userType = "MEMBER"
        val token = "valid.jwt.refresh.token"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val refreshToken = RefreshToken.reconstruct(id, identifier, userType, token, expiresAt, createdAt)

        val entity = RefreshTokenJpaEntity(
            id = id.value,
            identifier = identifier,
            userType = userType,
            token = token,
            expiresAt = expiresAt,
            createdAt = createdAt
        )

        `when`(mapper.toEntity(refreshToken)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(refreshToken)

        val result = adapter.save(refreshToken)

        assertEquals(refreshToken, result)
        assertEquals("3456789012345", result.identifier)
        assertEquals("MEMBER", result.userType)
    }
}
