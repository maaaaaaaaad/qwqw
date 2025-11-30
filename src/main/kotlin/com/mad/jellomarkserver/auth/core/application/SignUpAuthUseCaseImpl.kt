package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.core.domain.model.Auth
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.RawPassword
import com.mad.jellomarkserver.auth.core.domain.model.UserType
import com.mad.jellomarkserver.auth.port.driven.AuthPort
import com.mad.jellomarkserver.auth.port.driving.SignUpAuthCommand
import com.mad.jellomarkserver.auth.port.driving.SignUpAuthUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpAuthUseCaseImpl(
    private val authPort: AuthPort
) : SignUpAuthUseCase {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun signUp(command: SignUpAuthCommand): Auth {
        val email = AuthEmail.of(command.email)
        val rawPassword = RawPassword.of(command.password)
        val userType = UserType.valueOf(command.userType)
        val auth = Auth.create(email, rawPassword, userType)
        return authPort.save(auth)
    }
}
