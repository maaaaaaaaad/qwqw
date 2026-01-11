package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtProperties
import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtTokenProvider
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidTokenException
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken
import com.mad.jellomarkserver.auth.core.domain.model.TokenPair
import com.mad.jellomarkserver.auth.port.driven.RefreshTokenPort
import com.mad.jellomarkserver.auth.port.driving.RefreshTokenCommand
import com.mad.jellomarkserver.auth.port.driving.RefreshTokenUseCase
import org.springframework.stereotype.Service

@Service
class RefreshTokenUseCaseImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    private val refreshTokenPort: RefreshTokenPort
) : RefreshTokenUseCase {
    override fun execute(command: RefreshTokenCommand): TokenPair {
        if (!jwtTokenProvider.validateToken(command.refreshToken)) {
            throw InvalidTokenException("Invalid refresh token")
        }

        val identifier = jwtTokenProvider.getEmailFromToken(command.refreshToken)

        val savedRefreshToken = refreshTokenPort.findByIdentifier(identifier)
            ?: throw InvalidTokenException("Refresh token not found")

        if (savedRefreshToken.token != command.refreshToken) {
            throw InvalidTokenException("Refresh token mismatch")
        }

        if (savedRefreshToken.isExpired()) {
            throw InvalidTokenException("Refresh token expired")
        }

        val userType = savedRefreshToken.userType

        val newAccessToken = jwtTokenProvider.generateAccessToken(identifier, userType)
        val newRefreshTokenString = jwtTokenProvider.generateRefreshToken(identifier)

        refreshTokenPort.deleteByIdentifier(identifier)

        val newRefreshToken = RefreshToken.create(
            identifier = identifier,
            userType = userType,
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
