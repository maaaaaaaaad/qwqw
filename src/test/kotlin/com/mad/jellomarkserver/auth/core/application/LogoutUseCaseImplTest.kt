package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.port.driven.RefreshTokenPort
import com.mad.jellomarkserver.auth.port.driving.LogoutCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class LogoutUseCaseImplTest {

    @Mock
    private lateinit var refreshTokenPort: RefreshTokenPort

    private lateinit var useCase: LogoutUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = LogoutUseCaseImpl(refreshTokenPort)
    }

    @Test
    fun `should delete refresh token by identifier on logout`() {
        val command = LogoutCommand(identifier = "test@example.com")

        useCase.execute(command)

        verify(refreshTokenPort).deleteByIdentifier("test@example.com")
    }
}
