package com.mad.jellomarkserver.auth.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "refresh_tokens", uniqueConstraints = [
        UniqueConstraint(name = "uk_refresh_tokens_identifier", columnNames = ["identifier"]),
        UniqueConstraint(name = "uk_refresh_tokens_token", columnNames = ["token"])
    ]
)
class RefreshTokenJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "identifier", nullable = false, length = 255)
    var identifier: String,

    @Column(name = "user_type", nullable = false, length = 20)
    var userType: String,

    @Column(name = "token", nullable = false, length = 500)
    var token: String,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
