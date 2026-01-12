package com.mad.jellomarkserver.category.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "categories",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_categories_name", columnNames = ["name"])
    ]
)
class CategoryJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "name", nullable = false, length = 20)
    var name: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
