package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.review.port.driving.PagedReviews

data class PagedReviewsResponse(
    val items: List<ReviewResponse>,
    val hasNext: Boolean,
    val totalElements: Long
) {
    companion object {
        fun from(
            pagedReviews: PagedReviews,
            memberNicknames: Map<String, String> = emptyMap(),
            shopNames: Map<String, String> = emptyMap(),
            shopImages: Map<String, String> = emptyMap()
        ): PagedReviewsResponse {
            return PagedReviewsResponse(
                items = pagedReviews.items.map { review ->
                    val memberId = review.memberId.value.toString()
                    val shopId = review.shopId.value.toString()
                    ReviewResponse.from(
                        review = review,
                        authorName = memberNicknames[memberId],
                        shopName = shopNames[shopId],
                        shopImage = shopImages[shopId]
                    )
                },
                hasNext = pagedReviews.hasNext,
                totalElements = pagedReviews.totalElements
            )
        }
    }
}
