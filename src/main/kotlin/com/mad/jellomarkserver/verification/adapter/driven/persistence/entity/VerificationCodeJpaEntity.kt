package com.mad.jellomarkserver.verification.adapter.driven.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "verification_codes",
    indexes = [Index(name = "idx_verification_target_type", columnList = "target, type")]
)
class VerificationCodeJpaEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val target: String = "",

    @Column(nullable = false, length = 6)
    val code: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val type: VerificationTypeJpa = VerificationTypeJpa.EMAIL,

    @Column(nullable = false)
    val expiresAt: Instant = Instant.now(),

    @Column(nullable = false)
    var verified: Boolean = false,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)

enum class VerificationTypeJpa {
    EMAIL
}
