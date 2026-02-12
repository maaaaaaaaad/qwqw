package com.mad.jellomarkserver.review.port.driving

fun interface DeleteReviewReplyUseCase {
    fun execute(command: DeleteReviewReplyCommand)
}
