package com.mad.jellomarkserver.review.port.driving

import com.mad.jellomarkserver.review.core.domain.model.ShopReview

fun interface ListReviewsUseCase {
    fun execute(command: ListReviewsCommand): PagedReviews
}

data class PagedReviews(
    val items: List<ShopReview>,
    val hasNext: Boolean,
    val totalElements: Long
)
