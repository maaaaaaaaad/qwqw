package com.mad.jellomarkserver.review.port.driving

data class UpdateReviewCommand(
    val reviewId: String,
    val memberId: String,
    val rating: Int,
    val content: String,
    val images: List<String>?
)
