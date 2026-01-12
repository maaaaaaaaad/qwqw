package com.mad.jellomarkserver.review.port.driving

data class DeleteReviewCommand(
    val reviewId: String,
    val memberId: String
)
