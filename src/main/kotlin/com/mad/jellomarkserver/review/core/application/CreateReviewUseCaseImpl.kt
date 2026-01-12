package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsCommand
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsUseCase
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.core.domain.exception.DuplicateReviewException
import com.mad.jellomarkserver.review.core.domain.model.*
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.CreateReviewCommand
import com.mad.jellomarkserver.review.port.driving.CreateReviewUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CreateReviewUseCaseImpl(
    private val shopReviewPort: ShopReviewPort,
    private val updateBeautishopStatsUseCase: UpdateBeautishopStatsUseCase
) : CreateReviewUseCase {

    @Transactional
    override fun execute(command: CreateReviewCommand): ShopReview {
        val shopId = ShopId.from(UUID.fromString(command.shopId))
        val memberId = MemberId.from(UUID.fromString(command.memberId))

        if (shopReviewPort.existsByShopIdAndMemberId(shopId, memberId)) {
            throw DuplicateReviewException(command.shopId, command.memberId)
        }

        val review = ShopReview.create(
            shopId = shopId,
            memberId = memberId,
            rating = ReviewRating.of(command.rating),
            content = ReviewContent.of(command.content),
            images = command.images?.takeIf { it.isNotEmpty() }?.let { ReviewImages.of(it) }
        )

        val savedReview = shopReviewPort.save(review)
        updateBeautishopStatsUseCase.execute(UpdateBeautishopStatsCommand(command.shopId))
        return savedReview
    }
}
