package com.mad.jellomarkserver.review.core.domain.exception

class InvalidReviewContentException(content: String) : RuntimeException(
    "Invalid review content: content must be between 10 and 500 characters. Given length: ${content.length}"
)
