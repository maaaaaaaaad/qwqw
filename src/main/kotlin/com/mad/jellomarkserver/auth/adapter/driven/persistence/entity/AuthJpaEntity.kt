package com.mad.jellomarkserver.auth.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "auths", uniqueConstraints = [
        UniqueConstraint(name = "uk_auths_email", columnNames = ["email"])
    ]
)
class AuthJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "email", nullable = false, length = 255)
    var email: String,

    @Column(name = "hashed_password", nullable = false, length = 60)
    var hashedPassword: String,

    @Column(name = "user_type", nullable = false, length = 20)
    var userType: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
