package com.mad.jellomarkserver.favorite.core.domain.model

import java.util.*

@JvmInline
value class FavoriteId private constructor(val value: UUID) {
    companion object {
        fun new(): FavoriteId = FavoriteId(UUID.randomUUID())
        fun from(uuid: UUID): FavoriteId = FavoriteId(uuid)
    }
}
