package com.mad.jellomarkserver.review.port.driving

import com.mad.jellomarkserver.review.core.domain.model.ShopReview

fun interface CreateReviewUseCase {
    fun execute(command: CreateReviewCommand): ShopReview
}
