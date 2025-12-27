package com.mad.jellomarkserver.beautishop.core.domain.model

import java.util.*

@JvmInline
value class ShopId private constructor(val value: UUID) {
    companion object {
        fun new(): ShopId = ShopId(UUID.randomUUID())
        fun from(uuid: UUID): ShopId = ShopId(uuid)
    }
}
