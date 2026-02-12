package com.mad.jellomarkserver.review.core.domain.exception

class InvalidReplyContentException(content: String) : RuntimeException(
    "Invalid reply content: content must be between 1 and 500 characters. Given length: ${content.length}"
)
