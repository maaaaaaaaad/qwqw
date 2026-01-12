package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.category.core.domain.model.Category
import java.time.Instant

data class CategoryResponse(
    val id: String,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(category: Category): CategoryResponse {
            return CategoryResponse(
                id = category.id.value.toString(),
                name = category.name.value,
                createdAt = category.createdAt,
                updatedAt = category.updatedAt
            )
        }
    }
}
