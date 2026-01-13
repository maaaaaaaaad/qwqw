package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtProperties
import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtTokenProvider
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidTokenException
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken
import com.mad.jellomarkserver.auth.port.driven.RefreshTokenPort
import com.mad.jellomarkserver.auth.port.driving.RefreshTokenCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class RefreshTokenUseCaseImplTest {

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    private lateinit var jwtProperties: JwtProperties

    @Mock
    private lateinit var refreshTokenPort: RefreshTokenPort

    private lateinit var useCase: RefreshTokenUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = RefreshTokenUseCaseImpl(jwtTokenProvider, jwtProperties, refreshTokenPort)
    }

    @Test
    fun `should refresh token pair when valid refresh token provided`() {
        val refreshTokenString = "valid-refresh-token"
        val identifier = "test@example.com"
        val savedRefreshToken = RefreshToken.create(
            identifier = identifier, userType = "OWNER", token = refreshTokenString, expirationMillis = 604800000L
        )

        whenever(jwtTokenProvider.validateToken(refreshTokenString)).thenReturn(true)
        whenever(jwtTokenProvider.getEmailFromToken(refreshTokenString)).thenReturn(identifier)
        whenever(refreshTokenPort.findByIdentifier(identifier)).thenReturn(savedRefreshToken)
        whenever(jwtTokenProvider.generateAccessToken(identifier, "OWNER")).thenReturn("new-access-token")
        whenever(jwtTokenProvider.generateRefreshToken(identifier)).thenReturn("new-refresh-token")
        whenever(jwtProperties.refreshTokenExpiration).thenReturn(604800000L)

        val command = RefreshTokenCommand(refreshToken = refreshTokenString)
        val result = useCase.execute(command)

        assertEquals("new-access-token", result.accessToken)
        assertEquals("new-refresh-token", result.refreshToken)
        verify(refreshTokenPort).deleteByIdentifier(identifier)
        verify(refreshTokenPort).save(any())
    }

    @Test
    fun `should throw InvalidTokenException when token validation fails`() {
        val refreshTokenString = "invalid-token"
        whenever(jwtTokenProvider.validateToken(refreshTokenString)).thenReturn(false)

        val command = RefreshTokenCommand(refreshToken = refreshTokenString)

        val exception = assertThrows(InvalidTokenException::class.java) {
            useCase.execute(command)
        }
        assertTrue(exception.message!!.contains("Invalid refresh token"))
    }

    @Test
    fun `should throw InvalidTokenException when refresh token not found in database`() {
        val refreshTokenString = "valid-but-not-found"
        val identifier = "test@example.com"

        whenever(jwtTokenProvider.validateToken(refreshTokenString)).thenReturn(true)
        whenever(jwtTokenProvider.getEmailFromToken(refreshTokenString)).thenReturn(identifier)
        whenever(refreshTokenPort.findByIdentifier(identifier)).thenReturn(null)

        val command = RefreshTokenCommand(refreshToken = refreshTokenString)

        val exception = assertThrows(InvalidTokenException::class.java) {
            useCase.execute(command)
        }
        assertTrue(exception.message!!.contains("not found"))
    }

    @Test
    fun `should throw InvalidTokenException when refresh token does not match saved token`() {
        val refreshTokenString = "mismatched-token"
        val identifier = "test@example.com"
        val savedRefreshToken = RefreshToken.create(
            identifier = identifier, userType = "OWNER", token = "different-token", expirationMillis = 604800000L
        )

        whenever(jwtTokenProvider.validateToken(refreshTokenString)).thenReturn(true)
        whenever(jwtTokenProvider.getEmailFromToken(refreshTokenString)).thenReturn(identifier)
        whenever(refreshTokenPort.findByIdentifier(identifier)).thenReturn(savedRefreshToken)

        val command = RefreshTokenCommand(refreshToken = refreshTokenString)

        val exception = assertThrows(InvalidTokenException::class.java) {
            useCase.execute(command)
        }
        assertTrue(exception.message!!.contains("mismatch"))
    }
}
