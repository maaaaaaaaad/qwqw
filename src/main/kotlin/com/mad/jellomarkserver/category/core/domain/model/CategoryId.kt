package com.mad.jellomarkserver.category.core.domain.model

import java.util.*

@JvmInline
value class CategoryId private constructor(val value: UUID) {
    companion object {
        fun new(): CategoryId = CategoryId(UUID.randomUUID())
        fun from(uuid: UUID): CategoryId = CategoryId(uuid)
    }
}
