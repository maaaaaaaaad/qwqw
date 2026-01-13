package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.treatment.core.domain.model.Treatment
import java.time.Instant
import java.util.*

data class TreatmentResponse(
    val id: UUID,
    val shopId: UUID,
    val name: String,
    val price: Int,
    val duration: Int,
    val description: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(treatment: Treatment): TreatmentResponse {
            return TreatmentResponse(
                id = treatment.id.value,
                shopId = treatment.shopId.value,
                name = treatment.name.value,
                price = treatment.price.value,
                duration = treatment.duration.value,
                description = treatment.description?.value,
                createdAt = treatment.createdAt,
                updatedAt = treatment.updatedAt
            )
        }
    }
}
