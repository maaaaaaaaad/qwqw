package com.mad.jellomarkserver.notification.core.application

import com.mad.jellomarkserver.notification.core.domain.model.DevicePlatform
import com.mad.jellomarkserver.notification.core.domain.model.DeviceToken
import com.mad.jellomarkserver.notification.core.domain.model.UserRole
import com.mad.jellomarkserver.notification.port.driven.DeviceTokenPort
import com.mad.jellomarkserver.notification.port.driving.RegisterDeviceTokenCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

@ExtendWith(MockitoExtension::class)
class RegisterDeviceTokenUseCaseImplTest {

    @Mock
    private lateinit var deviceTokenPort: DeviceTokenPort

    private lateinit var useCase: RegisterDeviceTokenUseCaseImpl

    private val fixedClock = Clock.fixed(Instant.parse("2025-06-15T14:00:00Z"), ZoneId.of("UTC"))

    @BeforeEach
    fun setup() {
        useCase = RegisterDeviceTokenUseCaseImpl(deviceTokenPort, fixedClock)
    }

    @Test
    fun `should register new device token for member`() {
        whenever(deviceTokenPort.findByToken("fcm-token-123")).thenReturn(null)
        whenever(deviceTokenPort.save(any())).thenAnswer { it.arguments[0] as DeviceToken }

        val command = RegisterDeviceTokenCommand(
            userId = UUID.randomUUID().toString(),
            userRole = "MEMBER",
            token = "fcm-token-123",
            platform = "IOS"
        )

        val result = useCase.execute(command)

        assertEquals(UserRole.MEMBER, result.userRole)
        assertEquals("fcm-token-123", result.token)
        assertEquals(DevicePlatform.IOS, result.platform)
        verify(deviceTokenPort).save(any())
    }

    @Test
    fun `should register new device token for owner`() {
        whenever(deviceTokenPort.findByToken("fcm-token-456")).thenReturn(null)
        whenever(deviceTokenPort.save(any())).thenAnswer { it.arguments[0] as DeviceToken }

        val command = RegisterDeviceTokenCommand(
            userId = UUID.randomUUID().toString(),
            userRole = "OWNER",
            token = "fcm-token-456",
            platform = "ANDROID"
        )

        val result = useCase.execute(command)

        assertEquals(UserRole.OWNER, result.userRole)
        assertEquals("fcm-token-456", result.token)
        assertEquals(DevicePlatform.ANDROID, result.platform)
    }

    @Test
    fun `should delete existing token before saving new one`() {
        val existingToken = DeviceToken.reconstruct(
            id = com.mad.jellomarkserver.notification.core.domain.model.DeviceTokenId.new(),
            userId = UUID.randomUUID(),
            userRole = UserRole.MEMBER,
            token = "fcm-token-123",
            platform = DevicePlatform.IOS,
            createdAt = Instant.now()
        )
        whenever(deviceTokenPort.findByToken("fcm-token-123")).thenReturn(existingToken)
        whenever(deviceTokenPort.save(any())).thenAnswer { it.arguments[0] as DeviceToken }

        val command = RegisterDeviceTokenCommand(
            userId = UUID.randomUUID().toString(),
            userRole = "MEMBER",
            token = "fcm-token-123",
            platform = "IOS"
        )

        useCase.execute(command)

        verify(deviceTokenPort).deleteByToken("fcm-token-123")
        verify(deviceTokenPort).save(any())
    }
}
