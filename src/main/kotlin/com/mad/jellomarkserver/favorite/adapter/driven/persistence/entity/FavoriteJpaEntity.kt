package com.mad.jellomarkserver.favorite.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_favorites_member_shop", columnNames = ["member_id", "shop_id"])
    ],
    indexes = [
        Index(name = "idx_favorites_member_id", columnList = "member_id"),
        Index(name = "idx_favorites_shop_id", columnList = "shop_id"),
        Index(name = "idx_favorites_created_at", columnList = "created_at")
    ]
)
class FavoriteJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "member_id", nullable = false)
    var memberId: UUID,

    @Column(name = "shop_id", nullable = false)
    var shopId: UUID,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
