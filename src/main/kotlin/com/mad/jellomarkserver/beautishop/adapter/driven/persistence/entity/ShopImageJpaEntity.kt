package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "shop_images",
    indexes = [
        Index(name = "idx_shop_images_shop_id", columnList = "shop_id")
    ]
)
class ShopImageJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "shop_id", nullable = false)
    var shopId: UUID,

    @Column(name = "image_url", nullable = false, length = 2048)
    var imageUrl: String,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int
)
