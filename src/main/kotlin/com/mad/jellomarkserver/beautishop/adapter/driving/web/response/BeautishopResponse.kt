package com.mad.jellomarkserver.beautishop.adapter.driving.web.response

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.category.core.domain.model.Category
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
    val categories: List<CategorySummary>,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    data class CategorySummary(
        val id: String,
        val name: String
    ) {
        companion object {
            fun from(category: Category): CategorySummary {
                return CategorySummary(
                    id = category.id.value.toString(),
                    name = category.name.value
                )
            }
        }
    }

    companion object {
        fun from(beautishop: Beautishop, categories: List<Category> = emptyList()): BeautishopResponse {
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
                categories = categories.map { CategorySummary.from(it) },
                createdAt = beautishop.createdAt,
                updatedAt = beautishop.updatedAt
            )
        }
    }
}
