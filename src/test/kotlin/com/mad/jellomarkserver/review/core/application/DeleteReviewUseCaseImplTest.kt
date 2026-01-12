package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsUseCase
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.core.domain.exception.ReviewNotFoundException
import com.mad.jellomarkserver.review.core.domain.exception.UnauthorizedReviewAccessException
import com.mad.jellomarkserver.review.core.domain.model.*
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.DeleteReviewCommand
import com.mad.jellomarkserver.review.port.driving.DeleteReviewUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.never
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.Instant
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class DeleteReviewUseCaseImplTest {

    @Mock
    private lateinit var shopReviewPort: ShopReviewPort

    @Mock
    private lateinit var updateBeautishopStatsUseCase: UpdateBeautishopStatsUseCase

    private lateinit var useCase: DeleteReviewUseCase

    @BeforeEach
    fun setup() {
        useCase = DeleteReviewUseCaseImpl(shopReviewPort, updateBeautishopStatsUseCase)
    }

    @Test
    fun `should delete review successfully when owner requests`() {
        val reviewId = ReviewId.new()
        val memberId = MemberId.new()
        val existingReview = createReview(reviewId, memberId)

        `when`(shopReviewPort.findById(any())).thenReturn(existingReview)

        val command = DeleteReviewCommand(
            reviewId = reviewId.value.toString(),
            memberId = memberId.value.toString()
        )

        useCase.execute(command)

        verify(shopReviewPort).delete(any())
    }

    @Test
    fun `should throw ReviewNotFoundException when review does not exist`() {
        val reviewId = ReviewId.new()
        val memberId = MemberId.new()

        `when`(shopReviewPort.findById(any())).thenReturn(null)

        val command = DeleteReviewCommand(
            reviewId = reviewId.value.toString(),
            memberId = memberId.value.toString()
        )

        assertFailsWith<ReviewNotFoundException> {
            useCase.execute(command)
        }

        verify(shopReviewPort, never()).delete(any())
    }

    @Test
    fun `should throw UnauthorizedReviewAccessException when non-owner tries to delete`() {
        val reviewId = ReviewId.new()
        val ownerId = MemberId.new()
        val otherMemberId = MemberId.new()
        val existingReview = createReview(reviewId, ownerId)

        `when`(shopReviewPort.findById(any())).thenReturn(existingReview)

        val command = DeleteReviewCommand(
            reviewId = reviewId.value.toString(),
            memberId = otherMemberId.value.toString()
        )

        assertFailsWith<UnauthorizedReviewAccessException> {
            useCase.execute(command)
        }

        verify(shopReviewPort, never()).delete(any())
    }

    private fun createReview(reviewId: ReviewId, memberId: MemberId): ShopReview {
        return ShopReview.reconstruct(
            id = reviewId,
            shopId = ShopId.new(),
            memberId = memberId,
            rating = ReviewRating.of(5),
            content = ReviewContent.of("원본 리뷰 내용입니다. 정말 좋았어요!"),
            images = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
