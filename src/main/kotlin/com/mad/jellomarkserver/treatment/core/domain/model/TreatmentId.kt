package com.mad.jellomarkserver.treatment.core.domain.model

import java.util.*

@JvmInline
value class TreatmentId private constructor(val value: UUID) {
    companion object {
        fun new(): TreatmentId = TreatmentId(UUID.randomUUID())
        fun from(uuid: UUID): TreatmentId = TreatmentId(uuid)
    }
}
