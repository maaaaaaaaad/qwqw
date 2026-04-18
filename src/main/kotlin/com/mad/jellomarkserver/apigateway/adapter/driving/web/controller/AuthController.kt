package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.AuthenticateRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.LoginWithKakaoRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.RefreshTokenRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.ResetPasswordRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.AuthenticateResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.LoginWithKakaoResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.RefreshTokenResponse
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.HashedPassword
import com.mad.jellomarkserver.auth.core.domain.model.RawPassword
import com.mad.jellomarkserver.auth.port.driven.AuthPort
import com.mad.jellomarkserver.auth.port.driving.*
import com.mad.jellomarkserver.member.port.driving.LoginWithKakaoCommand
import com.mad.jellomarkserver.member.port.driving.LoginWithKakaoUseCase
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.verification.core.domain.exception.InvalidVerificationTokenException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authenticateUseCase: AuthenticateUseCase,
    private val issueTokenUseCase: IssueTokenUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val loginWithKakaoUseCase: LoginWithKakaoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authPort: AuthPort
) {

    @PostMapping("/api/auth/authenticate")
    @ResponseStatus(HttpStatus.OK)
    fun authenticate(@RequestBody request: AuthenticateRequest): AuthenticateResponse {
        val command = AuthenticateCommand(
            email = request.email,
            password = request.password
        )
        val auth = authenticateUseCase.authenticate(command)

        val tokenPair = issueTokenUseCase.execute(
            IssueTokenCommand(
                identifier = auth.email.value,
                userType = auth.userType.name
            )
        )

        return AuthenticateResponse(
            authenticated = true,
            email = auth.email.value,
            userType = auth.userType.name,
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken
        )
    }

    @PostMapping("/api/auth/refresh")
    @ResponseStatus(HttpStatus.OK)
    fun refresh(@RequestBody request: RefreshTokenRequest): RefreshTokenResponse {
        val command = RefreshTokenCommand(
            refreshToken = request.refreshToken
        )
        val tokenPair = refreshTokenUseCase.execute(command)

        return RefreshTokenResponse(
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken
        )
    }

    @PostMapping("/api/auth/kakao")
    @ResponseStatus(HttpStatus.OK)
    fun loginWithKakao(@RequestBody request: LoginWithKakaoRequest): LoginWithKakaoResponse {
        val command = LoginWithKakaoCommand(
            kakaoAccessToken = request.kakaoAccessToken
        )
        val tokenPair = loginWithKakaoUseCase.execute(command)

        return LoginWithKakaoResponse(
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken
        )
    }

    @PostMapping("/api/auth/reset-password")
    @ResponseStatus(HttpStatus.OK)
    fun resetPassword(@RequestBody request: ResetPasswordRequest) {
        if (request.emailVerificationToken.isBlank()) {
            throw InvalidVerificationTokenException()
        }

        val email = request.email.trim().lowercase()
        val auth = authPort.findByEmail(AuthEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val newHashedPassword = HashedPassword.fromRaw(RawPassword.of(request.newPassword))
        val updatedAuth = com.mad.jellomarkserver.auth.core.domain.model.Auth.reconstruct(
            id = auth.id,
            email = auth.email,
            hashedPassword = newHashedPassword,
            userType = auth.userType,
            createdAt = auth.createdAt,
            updatedAt = java.time.Instant.now()
        )
        authPort.save(updatedAuth)
    }

    @PostMapping("/api/auth/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Unit> {
        val socialId = request.getAttribute("socialId") as? String
            ?: request.getAttribute("email") as String

        logoutUseCase.execute(LogoutCommand(identifier = socialId))

        return ResponseEntity.ok().build()
    }
}
