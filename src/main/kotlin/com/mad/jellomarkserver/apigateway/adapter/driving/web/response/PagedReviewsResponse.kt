package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.review.port.driving.PagedReviews

data class PagedReviewsResponse(
    val items: List<ReviewResponse>,
    val hasNext: Boolean,
    val totalElements: Long
) {
    companion object {
        fun from(pagedReviews: PagedReviews, memberNicknames: Map<String, String> = emptyMap()): PagedReviewsResponse {
            return PagedReviewsResponse(
                items = pagedReviews.items.map { review ->
                    val memberId = review.memberId.value.toString()
                    ReviewResponse.from(review, memberNicknames[memberId])
                },
                hasNext = pagedReviews.hasNext,
                totalElements = pagedReviews.totalElements
            )
        }
    }
}
