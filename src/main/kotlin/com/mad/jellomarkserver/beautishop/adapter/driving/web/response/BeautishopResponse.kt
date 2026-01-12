package com.mad.jellomarkserver.beautishop.adapter.driving.web.response

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import java.time.Instant
import java.util.*

data class BeautishopResponse(
    val id: UUID,
    val name: String,
    val regNum: String,
    val phoneNumber: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val operatingTime: Map<String, String>,
    val description: String?,
    val image: String?,
    val averageRating: Double,
    val reviewCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(beautishop: Beautishop): BeautishopResponse {
            return BeautishopResponse(
                id = beautishop.id.value,
                name = beautishop.name.value,
                regNum = beautishop.regNum.value,
                phoneNumber = beautishop.phoneNumber.value,
                address = beautishop.address.value,
                latitude = beautishop.gps.latitude,
                longitude = beautishop.gps.longitude,
                operatingTime = beautishop.operatingTime.schedule,
                description = beautishop.description?.value,
                image = beautishop.image?.value,
                averageRating = beautishop.averageRating,
                reviewCount = beautishop.reviewCount,
                createdAt = beautishop.createdAt,
                updatedAt = beautishop.updatedAt
            )
        }
    }
}
