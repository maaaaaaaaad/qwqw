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
        Index(name = "idx_shop_reviews_member_id", columnList = "member_id"),
        Index(name = "idx_shop_reviews_created_at", columnList = "created_at"),
        Index(name = "idx_shop_reviews_rating", columnList = "rating")
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

    @Column(name = "rating", nullable = true)
    var rating: Int?,

    @Column(name = "content", nullable = true, length = 500)
    var content: String?,

    @Column(name = "images", nullable = true, columnDefinition = "TEXT")
    var images: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,

    @Column(name = "owner_reply_content", nullable = true, columnDefinition = "TEXT")
    var ownerReplyContent: String? = null,

    @Column(name = "owner_reply_created_at", nullable = true)
    var ownerReplyCreatedAt: Instant? = null
)
