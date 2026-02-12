package com.mad.jellomarkserver.review.port.driving

data class ReplyToReviewCommand(
    val shopId: String,
    val reviewId: String,
    val ownerEmail: String,
    val content: String
)
