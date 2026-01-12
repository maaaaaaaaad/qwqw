package com.mad.jellomarkserver.review.core.domain.exception

class ReviewNotFoundException(reviewId: String) : RuntimeException(
    "Review not found with id: $reviewId"
)
