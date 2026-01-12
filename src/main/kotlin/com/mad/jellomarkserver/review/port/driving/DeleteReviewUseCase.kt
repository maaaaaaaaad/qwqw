package com.mad.jellomarkserver.review.port.driving

fun interface DeleteReviewUseCase {
    fun execute(command: DeleteReviewCommand)
}
