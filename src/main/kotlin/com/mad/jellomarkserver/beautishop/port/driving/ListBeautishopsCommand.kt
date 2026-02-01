package com.mad.jellomarkserver.beautishop.port.driving

import java.util.*

data class ListBeautishopsCommand(
    val page: Int = 0,
    val size: Int = 20,
    val keyword: String? = null,
    val categoryId: UUID? = null,
    val minRating: Double? = null,
    val sortBy: SortBy = SortBy.CREATED_AT,
    val sortOrder: SortOrder = SortOrder.DESC,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusKm: Double? = null
)

enum class SortBy {
    RATING, REVIEW_COUNT, CREATED_AT, DISTANCE
}

enum class SortOrder {
    ASC, DESC
}
