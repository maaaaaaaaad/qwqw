package com.mad.jellomarkserver.review.core.domain.exception

class DuplicateReviewException(shopId: String, memberId: String) : RuntimeException(
    "Member $memberId has already reviewed shop $shopId"
)
