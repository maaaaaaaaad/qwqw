package com.mad.jellomarkserver.designer.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "designers",
    indexes = [
        Index(name = "idx_designers_shop_id", columnList = "shop_id")
    ]
)
class DesignerJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "shop_id", nullable = false)
    var shopId: UUID,

    @Column(name = "name", nullable = false, length = 30)
    var name: String,

    @Column(name = "nickname", nullable = true, length = 30)
    var nickname: String?,

    @Column(name = "intro", nullable = true, columnDefinition = "TEXT")
    var intro: String?,

    @Column(name = "photo_urls", nullable = true, columnDefinition = "TEXT")
    var photoUrls: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
