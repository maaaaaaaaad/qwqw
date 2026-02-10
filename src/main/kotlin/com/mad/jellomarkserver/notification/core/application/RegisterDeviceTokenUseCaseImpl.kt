package com.mad.jellomarkserver.notification.core.application

import com.mad.jellomarkserver.notification.core.domain.model.DevicePlatform
import com.mad.jellomarkserver.notification.core.domain.model.DeviceToken
import com.mad.jellomarkserver.notification.core.domain.model.UserRole
import com.mad.jellomarkserver.notification.port.driven.DeviceTokenPort
import com.mad.jellomarkserver.notification.port.driving.RegisterDeviceTokenCommand
import com.mad.jellomarkserver.notification.port.driving.RegisterDeviceTokenUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.*

@Service
class RegisterDeviceTokenUseCaseImpl(
    private val deviceTokenPort: DeviceTokenPort,
    private val clock: Clock = Clock.systemUTC()
) : RegisterDeviceTokenUseCase {

    @Transactional
    override fun execute(command: RegisterDeviceTokenCommand): DeviceToken {
        val existingToken = deviceTokenPort.findByToken(command.token)
        if (existingToken != null) {
            deviceTokenPort.deleteByToken(command.token)
        }

        val deviceToken = DeviceToken.create(
            userId = UUID.fromString(command.userId),
            userRole = UserRole.valueOf(command.userRole),
            token = command.token,
            platform = DevicePlatform.valueOf(command.platform),
            clock = clock
        )

        return deviceTokenPort.save(deviceToken)
    }
}
