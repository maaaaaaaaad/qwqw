package com.mad.jellomarkserver.auth.adapter.driven.jwt

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var jwtProperties: JwtProperties

    @BeforeEach
    fun setUp() {
        jwtProperties = JwtProperties(
            secretKey = "test-secret-key-for-testing-only-minimum-256-bits-required-for-hs256-algorithm",
            accessTokenExpiration = 3600000,
            refreshTokenExpiration = 604800000
        )
        jwtTokenProvider = JwtTokenProvider(jwtProperties)
    }

    @Test
    fun `should generate access token with email and userType`() {
        val email = "test@example.com"
        val userType = "MEMBER"

        val token = jwtTokenProvider.generateAccessToken(email, userType)

        assertThat(token).isNotBlank()
        assertThat(token.split(".").size).isEqualTo(3)
    }

    @Test
    fun `should generate refresh token with email`() {
        val email = "test@example.com"

        val token = jwtTokenProvider.generateRefreshToken(email)

        assertThat(token).isNotBlank()
        assertThat(token.split(".").size).isEqualTo(3)
    }

    @Test
    fun `should validate valid token`() {
        val email = "test@example.com"
        val userType = "MEMBER"
        val token = jwtTokenProvider.generateAccessToken(email, userType)

        val isValid = jwtTokenProvider.validateToken(token)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should reject invalid token`() {
        val invalidToken = "invalid.token.here"

        val isValid = jwtTokenProvider.validateToken(invalidToken)

        assertThat(isValid).isFalse()
    }

    @Test
    fun `should reject expired token`() {
        val expiredJwtProperties = JwtProperties(
            secretKey = "test-secret-key-for-testing-only-minimum-256-bits-required-for-hs256-algorithm",
            accessTokenExpiration = -1000,
            refreshTokenExpiration = 604800000
        )
        val expiredTokenProvider = JwtTokenProvider(expiredJwtProperties)
        val token = expiredTokenProvider.generateAccessToken("test@example.com", "MEMBER")

        val isValid = jwtTokenProvider.validateToken(token)

        assertThat(isValid).isFalse()
    }

    @Test
    fun `should extract email from token`() {
        val email = "test@example.com"
        val userType = "MEMBER"
        val token = jwtTokenProvider.generateAccessToken(email, userType)

        val extractedEmail = jwtTokenProvider.getEmailFromToken(token)

        assertThat(extractedEmail).isEqualTo(email)
    }

    @Test
    fun `should extract userType from access token`() {
        val email = "test@example.com"
        val userType = "MEMBER"
        val token = jwtTokenProvider.generateAccessToken(email, userType)

        val extractedUserType = jwtTokenProvider.getUserTypeFromToken(token)

        assertThat(extractedUserType).isEqualTo(userType)
    }
}
