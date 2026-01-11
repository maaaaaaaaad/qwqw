package com.mad.jellomarkserver.member.adapter.driven.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "members", uniqueConstraints = [
        UniqueConstraint(name = "uk_members_nickname", columnNames = ["nickname"]),
        UniqueConstraint(name = "uk_members_social", columnNames = ["social_provider", "social_id"]),
    ]
)
class MemberJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "social_provider", nullable = false, length = 20)
    var socialProvider: String,

    @Column(name = "social_id", nullable = false, length = 255)
    var socialId: String,

    @Column(name = "nickname", nullable = false, length = 100)
    var nickname: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
