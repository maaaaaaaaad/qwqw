package com.mad.jellomarkserver.notification.port.driving

import com.mad.jellomarkserver.notification.core.domain.model.DeviceToken

data class RegisterDeviceTokenCommand(
    val userId: String,
    val userRole: String,
    val token: String,
    val platform: String
)

fun interface RegisterDeviceTokenUseCase {
    fun execute(command: RegisterDeviceTokenCommand): DeviceToken
}
