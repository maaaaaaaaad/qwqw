package com.mad.jellomarkserver.review.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "shop_reviews",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_shop_reviews_shop_member", columnNames = ["shop_id", "member_id"])
    ],
    indexes = [
        Index(name = "idx_shop_reviews_shop_id", columnList = "shop_id"),
        Index(name = "idx_shop_reviews_member_id", columnList = "member_id")
    ]
)
class ShopReviewJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "shop_id", nullable = false)
    var shopId: UUID,

    @Column(name = "member_id", nullable = false)
    var memberId: UUID,

    @Column(name = "rating", nullable = false)
    var rating: Int,

    @Column(name = "content", nullable = false, length = 500)
    var content: String,

    @Column(name = "images", nullable = true, columnDefinition = "TEXT")
    var images: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
