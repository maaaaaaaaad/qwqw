package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.core.domain.exception.AuthenticationFailedException
import com.mad.jellomarkserver.auth.core.domain.model.Auth
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.RawPassword
import com.mad.jellomarkserver.auth.port.driven.AuthPort
import com.mad.jellomarkserver.auth.port.driving.AuthenticateCommand
import com.mad.jellomarkserver.auth.port.driving.AuthenticateUseCase
import org.springframework.stereotype.Service

@Service
class AuthenticateUseCaseImpl(
    private val authPort: AuthPort
) : AuthenticateUseCase {

    override fun authenticate(command: AuthenticateCommand): Auth {
        val email = AuthEmail.of(command.email)
        val auth = authPort.findByEmail(email)
            ?: throw AuthenticationFailedException(command.email)

        val rawPassword = RawPassword.of(command.password)
        if (!auth.hashedPassword.matches(rawPassword)) {
            throw AuthenticationFailedException(command.email)
        }

        return auth
    }
}
