package com.mad.jellomarkserver.category.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "shop_category_mappings",
    indexes = [
        Index(name = "idx_shop_category_shop_id", columnList = "shop_id"),
        Index(name = "idx_shop_category_category_id", columnList = "category_id")
    ]
)
class ShopCategoryMappingJpaEntity(
    @EmbeddedId
    var id: ShopCategoryMappingId,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
