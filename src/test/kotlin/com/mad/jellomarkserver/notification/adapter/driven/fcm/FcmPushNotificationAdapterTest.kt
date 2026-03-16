package com.mad.jellomarkserver.notification.adapter.driven.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class FcmPushNotificationAdapterTest {

    @Mock
    private lateinit var firebaseMessaging: FirebaseMessaging

    private lateinit var adapter: FcmPushNotificationAdapter

    @BeforeEach
    fun setup() {
        adapter = FcmPushNotificationAdapter(firebaseMessaging)
    }

    @Test
    fun `should send push notification successfully`() {
        `when`(firebaseMessaging.send(any(Message::class.java))).thenReturn("message-id")

        adapter.send(
            token = "test-token",
            title = "Test Title",
            body = "Test Body",
            data = mapOf("type" to "RESERVATION")
        )

        verify(firebaseMessaging).send(any(Message::class.java))
    }

    @Test
    fun `should propagate exception when send fails`() {
        `when`(firebaseMessaging.send(any(Message::class.java)))
            .thenThrow(RuntimeException("FCM send failed"))

        assertThrows<RuntimeException> {
            adapter.send(
                token = "invalid-token",
                title = "Test Title",
                body = "Test Body",
                data = emptyMap()
            )
        }
    }
}
