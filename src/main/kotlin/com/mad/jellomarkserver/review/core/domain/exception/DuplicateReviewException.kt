package com.mad.jellomarkserver.review.core.domain.exception

class DuplicateReviewException(reservationId: String) : RuntimeException(
    "Review already exists for reservation $reservationId"
)
