package com.mad.jellomarkserver.notification.port.driving

data class SendNotificationCommand(
    val userId: String,
    val userRole: String,
    val title: String,
    val body: String,
    val type: String,
    val data: Map<String, String> = emptyMap()
)

fun interface SendNotificationUseCase {
    fun execute(command: SendNotificationCommand)
}
