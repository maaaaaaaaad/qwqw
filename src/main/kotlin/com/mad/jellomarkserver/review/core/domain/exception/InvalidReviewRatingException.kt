package com.mad.jellomarkserver.review.core.domain.exception

class InvalidReviewRatingException(rating: Int) : RuntimeException(
    "Invalid review rating: $rating. Rating must be between 1 and 5."
)
