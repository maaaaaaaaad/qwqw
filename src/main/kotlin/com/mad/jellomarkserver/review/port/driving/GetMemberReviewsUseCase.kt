package com.mad.jellomarkserver.review.port.driving

fun interface GetMemberReviewsUseCase {
    fun execute(command: GetMemberReviewsCommand): PagedReviews
}

data class GetMemberReviewsCommand(
    val memberId: String,
    val page: Int = 0,
    val size: Int = 20,
    val sort: String = "createdAt,desc"
)
