package com.mad.jellomarkserver.review.core.domain.exception

class InvalidReviewImagesException(imageCount: Int) : RuntimeException(
    "Invalid review images: maximum 5 images allowed. Given: $imageCount"
)
