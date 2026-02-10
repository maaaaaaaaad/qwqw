package com.mad.jellomarkserver.notification.port.driven

interface PushNotificationPort {
    fun send(token: String, title: String, body: String, data: Map<String, String>)
}
