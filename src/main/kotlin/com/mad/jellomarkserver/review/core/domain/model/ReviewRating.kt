package com.mad.jellomarkserver.review.core.domain.model

import com.mad.jellomarkserver.review.core.domain.exception.InvalidReviewRatingException

@JvmInline
value class ReviewRating private constructor(val value: Int) {
    companion object {
        private const val MIN_RATING = 1
        private const val MAX_RATING = 5

        fun of(rating: Int): ReviewRating {
            if (rating < MIN_RATING || rating > MAX_RATING) {
                throw InvalidReviewRatingException(rating)
            }
            return ReviewRating(rating)
        }
    }
}
