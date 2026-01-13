package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtProperties
import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtTokenProvider
import com.mad.jellomarkserver.auth.port.driven.RefreshTokenPort
import com.mad.jellomarkserver.auth.port.driving.IssueTokenCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class IssueTokenUseCaseImplTest {

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    private lateinit var jwtProperties: JwtProperties

    @Mock
    private lateinit var refreshTokenPort: RefreshTokenPort

    private lateinit var useCase: IssueTokenUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = IssueTokenUseCaseImpl(jwtTokenProvider, jwtProperties, refreshTokenPort)
    }

    @Test
    fun `should issue token pair for owner with email identifier`() {
        whenever(jwtTokenProvider.generateAccessToken(any(), any())).thenReturn("access-token")
        whenever(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token")
        whenever(jwtProperties.refreshTokenExpiration).thenReturn(604800000L)

        val command = IssueTokenCommand(
            identifier = "test@example.com",
            userType = "OWNER"
        )
        val result = useCase.execute(command)

        assertEquals("access-token", result.accessToken)
        assertEquals("refresh-token", result.refreshToken)
        verify(refreshTokenPort).deleteByIdentifier("test@example.com")
        verify(refreshTokenPort).save(any())
    }

    @Test
    fun `should issue token pair for member with social login`() {
        whenever(jwtTokenProvider.generateAccessTokenForSocial(any(), any(), any())).thenReturn("social-access-token")
        whenever(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token")
        whenever(jwtProperties.refreshTokenExpiration).thenReturn(604800000L)

        val command = IssueTokenCommand(
            identifier = "12345",
            userType = "MEMBER",
            socialProvider = "KAKAO",
            socialId = "12345"
        )
        val result = useCase.execute(command)

        assertEquals("social-access-token", result.accessToken)
        assertEquals("refresh-token", result.refreshToken)
        verify(refreshTokenPort).deleteByIdentifier("12345")
        verify(refreshTokenPort).save(any())
    }
}
