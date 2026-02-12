package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.review.core.domain.exception.ReviewNotFoundException
import com.mad.jellomarkserver.review.core.domain.exception.UnauthorizedReviewAccessException
import com.mad.jellomarkserver.review.core.domain.model.ReplyContent
import com.mad.jellomarkserver.review.core.domain.model.ReviewId
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.ReplyToReviewCommand
import com.mad.jellomarkserver.review.port.driving.ReplyToReviewUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ReplyToReviewUseCaseImpl(
    private val ownerPort: OwnerPort,
    private val beautishopPort: BeautishopPort,
    private val shopReviewPort: ShopReviewPort
) : ReplyToReviewUseCase {

    @Transactional
    override fun execute(command: ReplyToReviewCommand) {
        val owner = ownerPort.findByEmail(OwnerEmail.of(command.ownerEmail))
            ?: throw OwnerNotFoundException(command.ownerEmail)

        val shopId = ShopId.from(UUID.fromString(command.shopId))
        val shopOwnerId = beautishopPort.findOwnerIdByShopId(shopId)

        if (shopOwnerId == null || shopOwnerId != owner.id) {
            throw UnauthorizedReviewAccessException(command.reviewId, command.ownerEmail)
        }

        val reviewId = ReviewId.from(UUID.fromString(command.reviewId))
        val review = shopReviewPort.findById(reviewId)
            ?: throw ReviewNotFoundException(command.reviewId)

        val replyContent = ReplyContent.of(command.content)
        val repliedReview = review.reply(replyContent)
        shopReviewPort.save(repliedReview)
    }
}
