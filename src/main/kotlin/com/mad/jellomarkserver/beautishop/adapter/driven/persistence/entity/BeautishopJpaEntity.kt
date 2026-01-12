package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "beautishops",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_beautishops_shop_reg_num", columnNames = ["shop_reg_num"])
    ],
    indexes = [
        Index(name = "idx_beautishops_owner_id", columnList = "owner_id")
    ]
)
class BeautishopJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "owner_id", nullable = false)
    var ownerId: UUID,

    @Column(name = "name", nullable = false, length = 50)
    var name: String,

    @Column(name = "shop_reg_num", nullable = false, length = 12)
    var shopRegNum: String,

    @Column(name = "phone_number", nullable = false, length = 13)
    var phoneNumber: String,

    @Column(name = "address", nullable = false, length = 200)
    var address: String,

    @Column(name = "latitude", nullable = false)
    var latitude: Double,

    @Column(name = "longitude", nullable = false)
    var longitude: Double,

    @Column(name = "operating_time", nullable = false, columnDefinition = "TEXT")
    var operatingTime: String,

    @Column(name = "description", nullable = true, length = 500)
    var description: String?,

    @Column(name = "image", nullable = true, length = 2048)
    var image: String?,

    @Column(name = "average_rating", nullable = false)
    var averageRating: Double = 0.0,

    @Column(name = "review_count", nullable = false)
    var reviewCount: Int = 0,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
