package com.mad.jellomarkserver.notification.adapter.driven.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.mad.jellomarkserver.notification.port.driven.PushNotificationPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FcmPushNotificationAdapter(
    private val firebaseMessaging: FirebaseMessaging
) : PushNotificationPort {

    private val log = LoggerFactory.getLogger(FcmPushNotificationAdapter::class.java)

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

        log.info("Sending FCM push to token={}..., title={}", token.take(10), title)
        val messageId = firebaseMessaging.send(message)
        log.info("FCM push sent successfully, messageId={}", messageId)
    }
}
