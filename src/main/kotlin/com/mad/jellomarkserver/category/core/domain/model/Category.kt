package com.mad.jellomarkserver.category.core.domain.model

import java.time.Clock
import java.time.Instant

class Category private constructor(
    val id: CategoryId,
    val name: CategoryName,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(
            name: CategoryName,
            clock: Clock = Clock.systemUTC()
        ): Category {
            val now = clock.instant()
            return Category(
                id = CategoryId.new(),
                name = name,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstruct(
            id: CategoryId,
            name: CategoryName,
            createdAt: Instant,
            updatedAt: Instant
        ): Category = Category(
            id = id,
            name = name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
