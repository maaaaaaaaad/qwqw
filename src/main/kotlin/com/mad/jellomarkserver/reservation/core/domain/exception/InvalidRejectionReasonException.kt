package com.mad.jellomarkserver.reservation.core.domain.exception

class InvalidRejectionReasonException(reason: String) : RuntimeException(
    "Invalid rejection reason: '$reason' (must be 1-200 characters)"
)
