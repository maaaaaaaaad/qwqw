package com.mad.jellomarkserver.review.core.domain.model

import com.mad.jellomarkserver.review.core.domain.exception.InvalidReviewImagesException

@JvmInline
value class ReviewImages private constructor(val urls: List<String>) {
    companion object {
        private const val MAX_IMAGES = 5

        fun of(urls: List<String>): ReviewImages {
            val validUrls = urls.filter { it.isNotBlank() }
            if (validUrls.size > MAX_IMAGES) {
                throw InvalidReviewImagesException(validUrls.size)
            }
            return ReviewImages(validUrls)
        }

        fun ofNullable(urls: List<String>?): ReviewImages? {
            if (urls.isNullOrEmpty()) {
                return null
            }
            return of(urls)
        }
    }
}
