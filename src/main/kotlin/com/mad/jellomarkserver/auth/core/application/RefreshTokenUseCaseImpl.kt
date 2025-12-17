package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtProperties
import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtTokenProvider
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidTokenException
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken
import com.mad.jellomarkserver.auth.core.domain.model.TokenPair
import com.mad.jellomarkserver.auth.port.driven.AuthPort
import com.mad.jellomarkserver.auth.port.driven.RefreshTokenPort
import com.mad.jellomarkserver.auth.port.driving.RefreshTokenCommand
import com.mad.jellomarkserver.auth.port.driving.RefreshTokenUseCase
import org.springframework.stereotype.Service

@Service
class RefreshTokenUseCaseImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    private val refreshTokenPort: RefreshTokenPort,
    private val authPort: AuthPort
) : RefreshTokenUseCase {
    override fun execute(command: RefreshTokenCommand): TokenPair {
        if (!jwtTokenProvider.validateToken(command.refreshToken)) {
            throw InvalidTokenException("Invalid refresh token")
        }

        val email = jwtTokenProvider.getEmailFromToken(command.refreshToken)
        val authEmail = AuthEmail.of(email)

        val savedRefreshToken = refreshTokenPort.findByEmail(authEmail)
            ?: throw InvalidTokenException("Refresh token not found")

        if (savedRefreshToken.token != command.refreshToken) {
            throw InvalidTokenException("Refresh token mismatch")
        }

        if (savedRefreshToken.isExpired()) {
            throw InvalidTokenException("Refresh token expired")
        }

        val auth = authPort.findByEmail(authEmail)
            ?: throw InvalidTokenException("Auth not found for email $email")

        val newAccessToken = jwtTokenProvider.generateAccessToken(email, auth.userType.name)
        val newRefreshTokenString = jwtTokenProvider.generateRefreshToken(email)

        refreshTokenPort.deleteByEmail(authEmail)

        val newRefreshToken = RefreshToken.create(
            email = authEmail,
            token = newRefreshTokenString,
            expirationMillis = jwtProperties.refreshTokenExpiration
        )
        refreshTokenPort.save(newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshTokenString
        )
    }
}
