package com.mad.jellomarkserver.notification.core.application

import com.mad.jellomarkserver.notification.core.domain.model.DevicePlatform
import com.mad.jellomarkserver.notification.core.domain.model.DeviceToken
import com.mad.jellomarkserver.notification.core.domain.model.DeviceTokenId
import com.mad.jellomarkserver.notification.core.domain.model.UserRole
import com.mad.jellomarkserver.notification.port.driven.DeviceTokenPort
import com.mad.jellomarkserver.notification.port.driven.PushNotificationPort
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class SendNotificationUseCaseImplTest {

    @Mock
    private lateinit var deviceTokenPort: DeviceTokenPort

    @Mock
    private lateinit var pushNotificationPort: PushNotificationPort

    private lateinit var useCase: SendNotificationUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = SendNotificationUseCaseImpl(deviceTokenPort, pushNotificationPort)
    }

    @Test
    fun `should send notification to all user devices`() {
        val userId = UUID.randomUUID()
        val token1 = createDeviceToken(userId, "token-1", DevicePlatform.IOS)
        val token2 = createDeviceToken(userId, "token-2", DevicePlatform.ANDROID)

        whenever(deviceTokenPort.findByUserIdAndUserRole(userId, UserRole.MEMBER))
            .thenReturn(listOf(token1, token2))

        val command = SendNotificationCommand(
            userId = userId.toString(),
            userRole = "MEMBER",
            title = "새 예약",
            body = "예약이 확정되었습니다",
            type = "RESERVATION_CONFIRMED",
            data = mapOf("reservationId" to "some-id")
        )

        useCase.execute(command)

        verify(pushNotificationPort).send(
            eq("token-1"),
            eq("새 예약"),
            eq("예약이 확정되었습니다"),
            eq(mapOf("type" to "RESERVATION_CONFIRMED", "reservationId" to "some-id"))
        )
        verify(pushNotificationPort).send(
            eq("token-2"),
            eq("새 예약"),
            eq("예약이 확정되었습니다"),
            eq(mapOf("type" to "RESERVATION_CONFIRMED", "reservationId" to "some-id"))
        )
    }

    @Test
    fun `should not fail when user has no devices`() {
        val userId = UUID.randomUUID()
        whenever(deviceTokenPort.findByUserIdAndUserRole(userId, UserRole.OWNER))
            .thenReturn(emptyList())

        val command = SendNotificationCommand(
            userId = userId.toString(),
            userRole = "OWNER",
            title = "Title",
            body = "Body",
            type = "RESERVATION_CREATED"
        )

        useCase.execute(command)

        verify(pushNotificationPort, never()).send(any(), any(), any(), any())
    }

    @Test
    fun `should delete stale token when send fails`() {
        val userId = UUID.randomUUID()
        val staleToken = createDeviceToken(userId, "stale-token", DevicePlatform.IOS)

        whenever(deviceTokenPort.findByUserIdAndUserRole(userId, UserRole.MEMBER))
            .thenReturn(listOf(staleToken))
        whenever(pushNotificationPort.send(eq("stale-token"), any(), any(), any()))
            .thenThrow(RuntimeException("Invalid registration token"))

        val command = SendNotificationCommand(
            userId = userId.toString(),
            userRole = "MEMBER",
            title = "Title",
            body = "Body",
            type = "RESERVATION_CONFIRMED"
        )

        useCase.execute(command)

        verify(deviceTokenPort).deleteByToken("stale-token")
    }

    private fun createDeviceToken(userId: UUID, token: String, platform: DevicePlatform): DeviceToken {
        return DeviceToken.reconstruct(
            id = DeviceTokenId.new(),
            userId = userId,
            userRole = UserRole.MEMBER,
            token = token,
            platform = platform,
            createdAt = Instant.now()
        )
    }
}
