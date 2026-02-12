package com.mad.jellomarkserver.review.port.driving

data class DeleteReviewReplyCommand(
    val shopId: String,
    val reviewId: String,
    val ownerEmail: String
)
