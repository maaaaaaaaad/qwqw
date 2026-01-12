package com.mad.jellomarkserver.review.core.domain.model

import com.mad.jellomarkserver.review.core.domain.exception.InvalidReviewContentException

@JvmInline
value class ReviewContent private constructor(val value: String) {
    companion object {
        private const val MIN_LENGTH = 10
        private const val MAX_LENGTH = 500

        fun of(content: String): ReviewContent {
            val trimmed = content.trim()
            if (trimmed.length !in MIN_LENGTH..MAX_LENGTH) {
                throw InvalidReviewContentException(trimmed)
            }
            return ReviewContent(trimmed)
        }
    }
}
