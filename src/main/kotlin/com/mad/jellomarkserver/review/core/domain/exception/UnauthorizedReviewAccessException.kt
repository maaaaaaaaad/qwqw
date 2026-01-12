package com.mad.jellomarkserver.review.core.domain.exception

class UnauthorizedReviewAccessException(reviewId: String, memberId: String) : RuntimeException(
    "Member $memberId is not authorized to access review $reviewId"
)
