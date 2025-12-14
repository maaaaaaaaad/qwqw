package com.mad.jellomarkserver.auth.adapter.driven.persistence.repository

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.RefreshTokenJpaEntity
import com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper.RefreshTokenMapper
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
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
        val email = AuthEmail.of("test@example.com")
        val token = "valid.jwt.refresh.token"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val refreshToken = RefreshToken.reconstruct(id, email, token, expiresAt, createdAt)

        val entity = RefreshTokenJpaEntity(
            id = id.value,
            email = email.value,
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
    fun `should find refresh token by email`() {
        val email = AuthEmail.of("test@example.com")
        val id = RefreshTokenId.from(UUID.randomUUID())
        val token = "valid.jwt.refresh.token"
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val refreshToken = RefreshToken.reconstruct(id, email, token, expiresAt, createdAt)

        val entity = RefreshTokenJpaEntity(
            id = id.value,
            email = email.value,
            token = token,
            expiresAt = expiresAt,
            createdAt = createdAt
        )

        `when`(jpaRepository.findByEmail(email.value)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(refreshToken)

        val result = adapter.findByEmail(email)

        assertEquals(refreshToken, result)
        verify(jpaRepository).findByEmail(email.value)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should return null when refresh token not found by email`() {
        val email = AuthEmail.of("notfound@example.com")

        `when`(jpaRepository.findByEmail(email.value)).thenReturn(null)

        val result = adapter.findByEmail(email)

        assertNull(result)
        verify(jpaRepository).findByEmail(email.value)
    }

    @Test
    fun `should find refresh token by token`() {
        val token = "valid.jwt.refresh.token"
        val id = RefreshTokenId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val expiresAt = Instant.parse("2025-12-20T00:00:00Z")
        val createdAt = Instant.parse("2025-12-13T00:00:00Z")
        val refreshToken = RefreshToken.reconstruct(id, email, token, expiresAt, createdAt)

        val entity = RefreshTokenJpaEntity(
            id = id.value,
            email = email.value,
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
    fun `should delete refresh token by email`() {
        val email = AuthEmail.of("test@example.com")

        adapter.deleteByEmail(email)

        verify(jpaRepository).deleteByEmail(email.value)
    }
}
