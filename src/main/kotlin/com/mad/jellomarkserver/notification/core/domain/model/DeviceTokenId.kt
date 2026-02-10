package com.mad.jellomarkserver.notification.core.domain.model

import java.util.*

@JvmInline
value class DeviceTokenId private constructor(val value: UUID) {
    companion object {
        fun new(): DeviceTokenId = DeviceTokenId(UUID.randomUUID())
        fun from(uuid: UUID): DeviceTokenId = DeviceTokenId(uuid)
    }
}
