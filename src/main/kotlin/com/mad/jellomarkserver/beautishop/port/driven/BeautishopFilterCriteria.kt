package com.mad.jellomarkserver.beautishop.port.driven

import com.mad.jellomarkserver.beautishop.port.driving.SortBy
import com.mad.jellomarkserver.beautishop.port.driving.SortOrder
import java.util.*

data class BeautishopFilterCriteria(
    val keyword: String? = null,
    val categoryId: UUID? = null,
    val minRating: Double? = null,
    val sortBy: SortBy = SortBy.CREATED_AT,
    val sortOrder: SortOrder = SortOrder.DESC,
    val latitude: Double? = null,
    val longitude: Double? = null
)
