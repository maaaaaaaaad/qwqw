package com.mad.jellomarkserver.owner.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "owners", uniqueConstraints = [
        UniqueConstraint(name = "uk_owners_business_number", columnNames = ["business_number"]),
        UniqueConstraint(name = "uk_owners_phone_number", columnNames = ["phone_number"]),
        UniqueConstraint(name = "uk_owners_nickname", columnNames = ["nickname"]),
        UniqueConstraint(name = "uk_owners_email", columnNames = ["email"]),
    ]
)
class OwnerJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "business_number", nullable = false, length = 9)
    var businessNumber: String,

    @Column(name = "phone_number", nullable = false, length = 13)
    var phoneNumber: String,

    @Column(name = "nickname", nullable = false, length = 100)
    var nickname: String,

    @Column(name = "email", nullable = false, length = 255)
    var email: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
