package com.mad.jellomarkserver.notification.port.driving

data class UnregisterDeviceTokenCommand(
    val token: String
)

fun interface UnregisterDeviceTokenUseCase {
    fun execute(command: UnregisterDeviceTokenCommand)
}
