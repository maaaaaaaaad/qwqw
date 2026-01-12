package com.mad.jellomarkserver.review.core.domain.model

import java.util.*

@JvmInline
value class ReviewId private constructor(val value: UUID) {
    companion object {
        fun new(): ReviewId = ReviewId(UUID.randomUUID())
        fun from(uuid: UUID): ReviewId = ReviewId(uuid)
    }
}
