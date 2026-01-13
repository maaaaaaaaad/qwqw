package com.mad.jellomarkserver.treatment.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "treatments",
    indexes = [
        Index(name = "idx_treatments_shop_id", columnList = "shop_id")
    ]
)
class TreatmentJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "shop_id", nullable = false)
    var shopId: UUID,

    @Column(name = "name", nullable = false, length = 50)
    var name: String,

    @Column(name = "price", nullable = false)
    var price: Int,

    @Column(name = "duration", nullable = false)
    var duration: Int,

    @Column(name = "description", nullable = true, length = 500)
    var description: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
