package com.mad.jellomarkserver.beautishop.core.domain.model

@JvmInline
value class AverageRating private constructor(val value: Double) {
    companion object {
        fun of(value: Double): AverageRating {
            require(value in 0.0..5.0) { "Rating must be between 0.0 and 5.0" }
            return AverageRating(value)
        }

        fun zero(): AverageRating = AverageRating(0.0)
    }
}
