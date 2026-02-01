package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.review.core.domain.model.ShopReview
import java.time.Instant

data class ReviewResponse(
    val id: String,
    val shopId: String,
    val shopName: String?,
    val shopImage: String?,
    val memberId: String,
    val authorName: String?,
    val rating: Int?,
    val content: String?,
    val images: List<String>?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(
            review: ShopReview,
            authorName: String? = null,
            shopName: String? = null,
            shopImage: String? = null
        ): ReviewResponse {
            return ReviewResponse(
                id = review.id.value.toString(),
                shopId = review.shopId.value.toString(),
                shopName = shopName,
                shopImage = shopImage,
                memberId = review.memberId.value.toString(),
                authorName = authorName,
                rating = review.rating?.value,
                content = review.content?.value,
                images = review.images?.urls,
                createdAt = review.createdAt,
                updatedAt = review.updatedAt
            )
        }
    }
}
