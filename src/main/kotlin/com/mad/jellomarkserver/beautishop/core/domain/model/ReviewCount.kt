package com.mad.jellomarkserver.beautishop.core.domain.model

@JvmInline
value class ReviewCount private constructor(val value: Int) {
    companion object {
        fun of(value: Int): ReviewCount {
            require(value >= 0) { "Review count cannot be negative" }
            return ReviewCount(value)
        }

        fun zero(): ReviewCount = ReviewCount(0)
    }
}
