package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.core.domain.exception.AuthenticationFailedException
import com.mad.jellomarkserver.auth.core.domain.model.Auth
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.RawPassword
import com.mad.jellomarkserver.auth.core.domain.model.UserType
import com.mad.jellomarkserver.auth.port.driven.AuthPort
import com.mad.jellomarkserver.auth.port.driving.AuthenticateCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class AuthenticateUseCaseImplTest {

    @Mock
    private lateinit var authPort: AuthPort

    private lateinit var useCase: AuthenticateUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = AuthenticateUseCaseImpl(authPort)
    }

    @Test
    fun `should authenticate successfully with valid credentials`() {
        val auth = Auth.create(
            email = AuthEmail.of("test@example.com"),
            rawPassword = RawPassword.of("Password123!"),
            userType = UserType.OWNER
        )
        whenever(authPort.findByEmail(any())).thenReturn(auth)

        val command = AuthenticateCommand(email = "test@example.com", password = "Password123!")
        val result = useCase.authenticate(command)

        assertEquals("test@example.com", result.email.value)
        assertEquals(UserType.OWNER, result.userType)
    }

    @Test
    fun `should throw AuthenticationFailedException when email not found`() {
        whenever(authPort.findByEmail(any())).thenReturn(null)

        val command = AuthenticateCommand(email = "nonexistent@example.com", password = "Password123!")

        val exception = assertThrows(AuthenticationFailedException::class.java) {
            useCase.authenticate(command)
        }
        assertTrue(exception.message!!.contains("nonexistent@example.com"))
    }

    @Test
    fun `should throw AuthenticationFailedException when password is wrong`() {
        val auth = Auth.create(
            email = AuthEmail.of("test@example.com"),
            rawPassword = RawPassword.of("Password123!"),
            userType = UserType.OWNER
        )
        whenever(authPort.findByEmail(any())).thenReturn(auth)

        val command = AuthenticateCommand(email = "test@example.com", password = "WrongPassword123!")

        val exception = assertThrows(AuthenticationFailedException::class.java) {
            useCase.authenticate(command)
        }
        assertTrue(exception.message!!.contains("test@example.com"))
    }
}
