package com.mad.jellomarkserver.review.core.domain.exception

class EmptyReviewException : RuntimeException(
    "A review must have at least rating or content"
)
