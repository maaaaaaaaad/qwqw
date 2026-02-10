package com.mad.jellomarkserver.usagehistory.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "usage_histories",
    indexes = [
        Index(name = "idx_usage_histories_member_id", columnList = "member_id"),
        Index(name = "idx_usage_histories_shop_id", columnList = "shop_id"),
    ]
)
class UsageHistoryJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "member_id", nullable = false)
    var memberId: UUID,

    @Column(name = "shop_id", nullable = false)
    var shopId: UUID,

    @Column(name = "reservation_id", nullable = false)
    var reservationId: UUID,

    @Column(name = "shop_name", nullable = false, length = 100)
    var shopName: String,

    @Column(name = "treatment_name", nullable = false, length = 50)
    var treatmentName: String,

    @Column(name = "treatment_price", nullable = false)
    var treatmentPrice: Int,

    @Column(name = "treatment_duration", nullable = false)
    var treatmentDuration: Int,

    @Column(name = "completed_at", nullable = false)
    var completedAt: Instant,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
