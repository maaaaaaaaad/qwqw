package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsCommand
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsUseCase
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.core.domain.exception.ReviewNotFoundException
import com.mad.jellomarkserver.review.core.domain.exception.UnauthorizedReviewAccessException
import com.mad.jellomarkserver.review.core.domain.model.*
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.UpdateReviewCommand
import com.mad.jellomarkserver.review.port.driving.UpdateReviewUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UpdateReviewUseCaseImpl(
    private val shopReviewPort: ShopReviewPort,
    private val updateBeautishopStatsUseCase: UpdateBeautishopStatsUseCase
) : UpdateReviewUseCase {

    @Transactional
    override fun execute(command: UpdateReviewCommand): ShopReview {
        val reviewId = ReviewId.from(UUID.fromString(command.reviewId))
        val memberId = MemberId.from(UUID.fromString(command.memberId))

        val existingReview = shopReviewPort.findById(reviewId)
            ?: throw ReviewNotFoundException(command.reviewId)

        if (!existingReview.isOwnedBy(memberId)) {
            throw UnauthorizedReviewAccessException(command.reviewId, command.memberId)
        }

        val updatedReview = existingReview.update(
            rating = ReviewRating.of(command.rating),
            content = ReviewContent.of(command.content),
            images = command.images?.takeIf { it.isNotEmpty() }?.let { ReviewImages.of(it) }
        )

        val savedReview = shopReviewPort.save(updatedReview)
        updateBeautishopStatsUseCase.execute(UpdateBeautishopStatsCommand(existingReview.shopId.value.toString()))
        return savedReview
    }
}
