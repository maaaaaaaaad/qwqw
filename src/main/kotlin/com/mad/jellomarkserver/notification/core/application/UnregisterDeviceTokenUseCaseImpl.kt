package com.mad.jellomarkserver.notification.core.application

import com.mad.jellomarkserver.notification.port.driven.DeviceTokenPort
import com.mad.jellomarkserver.notification.port.driving.UnregisterDeviceTokenCommand
import com.mad.jellomarkserver.notification.port.driving.UnregisterDeviceTokenUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UnregisterDeviceTokenUseCaseImpl(
    private val deviceTokenPort: DeviceTokenPort
) : UnregisterDeviceTokenUseCase {

    @Transactional
    override fun execute(command: UnregisterDeviceTokenCommand) {
        deviceTokenPort.deleteByToken(command.token)
    }
}
