package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsCommand
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsUseCase
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.core.domain.exception.ReviewNotFoundException
import com.mad.jellomarkserver.review.core.domain.exception.UnauthorizedReviewAccessException
import com.mad.jellomarkserver.review.core.domain.model.ReviewId
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.DeleteReviewCommand
import com.mad.jellomarkserver.review.port.driving.DeleteReviewUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DeleteReviewUseCaseImpl(
    private val shopReviewPort: ShopReviewPort,
    private val updateBeautishopStatsUseCase: UpdateBeautishopStatsUseCase
) : DeleteReviewUseCase {

    @Transactional
    override fun execute(command: DeleteReviewCommand) {
        val reviewId = ReviewId.from(UUID.fromString(command.reviewId))
        val memberId = MemberId.from(UUID.fromString(command.memberId))

        val existingReview = shopReviewPort.findById(reviewId)
            ?: throw ReviewNotFoundException(command.reviewId)

        if (!existingReview.isOwnedBy(memberId)) {
            throw UnauthorizedReviewAccessException(command.reviewId, command.memberId)
        }

        val shopId = existingReview.shopId.value.toString()
        shopReviewPort.delete(reviewId)
        updateBeautishopStatsUseCase.execute(UpdateBeautishopStatsCommand(shopId))
    }
}
