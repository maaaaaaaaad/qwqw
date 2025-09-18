package com.mad.jellomarkserver.member.adapter.out.persistence.entity

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
        UniqueConstraint(name = "uk_members_email", columnNames = ["email"]),
        UniqueConstraint(
            name = "uk_members_business_registration_number",
            columnNames = ["business_registration_number"]
        )
    ]
)
class MemberJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "nickname", nullable = false, length = 100)
    var nickname: String,

    @Column(name = "email", nullable = false, length = 255)
    var email: String,

    @Column(name = "member_type", nullable = false, length = 20)
    var memberType: String,

    @Column(name = "business_registration_number", length = 20)
    var businessRegistrationNumber: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)