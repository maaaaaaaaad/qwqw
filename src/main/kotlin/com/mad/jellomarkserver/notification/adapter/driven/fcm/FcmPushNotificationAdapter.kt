package com.mad.jellomarkserver.notification.adapter.driven.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.mad.jellomarkserver.notification.port.driven.PushNotificationPort
import org.springframework.stereotype.Component

@Component
class FcmPushNotificationAdapter(
    private val firebaseMessaging: FirebaseMessaging
) : PushNotificationPort {

    override fun send(token: String, title: String, body: String, data: Map<String, String>) {
        val message = Message.builder()
            .setToken(token)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build()
            )
            .putAllData(data)
            .build()

        firebaseMessaging.send(message)
    }
}
