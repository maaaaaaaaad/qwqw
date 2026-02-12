package com.mad.jellomarkserver.review.port.driving

fun interface ReplyToReviewUseCase {
    fun execute(command: ReplyToReviewCommand)
}
