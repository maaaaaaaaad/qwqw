package com.mad.jellomarkserver.notification.core.application

import com.mad.jellomarkserver.notification.port.driven.DeviceTokenPort
import com.mad.jellomarkserver.notification.port.driving.UnregisterDeviceTokenCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class UnregisterDeviceTokenUseCaseImplTest {

    @Mock
    private lateinit var deviceTokenPort: DeviceTokenPort

    private lateinit var useCase: UnregisterDeviceTokenUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = UnregisterDeviceTokenUseCaseImpl(deviceTokenPort)
    }

    @Test
    fun `should delete device token by token string`() {
        val command = UnregisterDeviceTokenCommand(token = "fcm-token-to-delete")

        useCase.execute(command)

        verify(deviceTokenPort).deleteByToken("fcm-token-to-delete")
    }
}
