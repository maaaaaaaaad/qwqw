package com.mad.jellomarkserver.review.port.driving

data class ListReviewsCommand(
    val shopId: String,
    val page: Int = 0,
    val size: Int = 20
)
