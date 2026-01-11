package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtProperties
import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtTokenProvider
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken
import com.mad.jellomarkserver.auth.core.domain.model.TokenPair
import com.mad.jellomarkserver.auth.port.driven.RefreshTokenPort
import com.mad.jellomarkserver.auth.port.driving.IssueTokenCommand
import com.mad.jellomarkserver.auth.port.driving.IssueTokenUseCase
import org.springframework.stereotype.Service

@Service
class IssueTokenUseCaseImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    private val refreshTokenPort: RefreshTokenPort
) : IssueTokenUseCase {
    override fun execute(command: IssueTokenCommand): TokenPair {
        val accessToken = jwtTokenProvider.generateAccessToken(command.identifier, command.userType)
        val refreshTokenString = jwtTokenProvider.generateRefreshToken(command.identifier)

        refreshTokenPort.deleteByIdentifier(command.identifier)

        val refreshToken = RefreshToken.create(
            identifier = command.identifier,
            userType = command.userType,
            token = refreshTokenString,
            expirationMillis = jwtProperties.refreshTokenExpiration
        )
        refreshTokenPort.save(refreshToken)

        return TokenPair(
            accessToken = accessToken,
            refreshToken = refreshTokenString
        )
    }
}
