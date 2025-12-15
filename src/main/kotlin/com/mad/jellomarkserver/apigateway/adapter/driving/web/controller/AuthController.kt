package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.AuthenticateRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.AuthenticateResponse
import com.mad.jellomarkserver.auth.port.driving.AuthenticateCommand
import com.mad.jellomarkserver.auth.port.driving.AuthenticateUseCase
import com.mad.jellomarkserver.auth.port.driving.IssueTokenCommand
import com.mad.jellomarkserver.auth.port.driving.IssueTokenUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authenticateUseCase: AuthenticateUseCase,
    private val issueTokenUseCase: IssueTokenUseCase
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
                email = auth.email.value,
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
}
