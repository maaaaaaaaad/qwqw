package com.mad.jellomarkserver.designer.core.domain.model

import java.util.*

@JvmInline
value class DesignerId private constructor(val value: UUID) {
    companion object {
        fun new(): DesignerId = DesignerId(UUID.randomUUID())
        fun from(uuid: UUID): DesignerId = DesignerId(uuid)
    }
}
