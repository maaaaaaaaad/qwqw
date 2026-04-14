package com.mad.jellomarkserver.externalshop.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "external_shops",
    indexes = [
        Index(name = "idx_external_shops_external_id", columnList = "external_id"),
        Index(name = "idx_external_shops_lat_lng", columnList = "latitude, longitude")
    ]
)
class ExternalShopJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "external_id", nullable = false, unique = true)
    var externalId: String = "",

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "address", nullable = false)
    var address: String = "",

    @Column(name = "latitude", nullable = false)
    var latitude: Double = 0.0,

    @Column(name = "longitude", nullable = false)
    var longitude: Double = 0.0,

    @Column(name = "category", nullable = false)
    var category: String = "",

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "last_updated", nullable = false)
    var lastUpdated: Instant = Instant.now(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now()
)
