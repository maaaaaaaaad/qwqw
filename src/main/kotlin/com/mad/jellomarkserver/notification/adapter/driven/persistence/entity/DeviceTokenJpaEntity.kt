package com.mad.jellomarkserver.notification.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "device_tokens",
    indexes = [
        Index(name = "idx_device_tokens_user", columnList = "user_id, user_role"),
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_device_tokens_token", columnNames = ["token"])
    ]
)
class DeviceTokenJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "user_role", nullable = false, length = 10)
    var userRole: String,

    @Column(name = "token", nullable = false, length = 512)
    var token: String,

    @Column(name = "platform", nullable = false, length = 10)
    var platform: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
