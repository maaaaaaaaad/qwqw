package com.mad.jellomarkserver.notification.core.application

import com.mad.jellomarkserver.notification.core.domain.model.UserRole
import com.mad.jellomarkserver.notification.port.driven.DeviceTokenPort
import com.mad.jellomarkserver.notification.port.driven.PushNotificationPort
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class SendNotificationUseCaseImpl(
    private val deviceTokenPort: DeviceTokenPort,
    private val pushNotificationPort: PushNotificationPort
) : SendNotificationUseCase {

    private val log = LoggerFactory.getLogger(SendNotificationUseCaseImpl::class.java)

    override fun execute(command: SendNotificationCommand) {
        val userId = UUID.fromString(command.userId)
        val userRole = UserRole.valueOf(command.userRole)

        val deviceTokens = deviceTokenPort.findByUserIdAndUserRole(userId, userRole)
        if (deviceTokens.isEmpty()) {
            log.debug("No device tokens found for user {} ({})", command.userId, command.userRole)
            return
        }

        val payload = buildMap {
            put("type", command.type)
            putAll(command.data)
        }

        for (deviceToken in deviceTokens) {
            try {
                pushNotificationPort.send(
                    token = deviceToken.token,
                    title = command.title,
                    body = command.body,
                    data = payload
                )
            } catch (e: Exception) {
                log.warn("Failed to send push to token {}: {}", deviceToken.token, e.message)
                deviceTokenPort.deleteByToken(deviceToken.token)
            }
        }
    }
}
