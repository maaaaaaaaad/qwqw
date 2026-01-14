package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsUseCase
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.core.domain.exception.InvalidReviewContentException
import com.mad.jellomarkserver.review.core.domain.exception.InvalidReviewRatingException
import com.mad.jellomarkserver.review.core.domain.exception.ReviewNotFoundException
import com.mad.jellomarkserver.review.core.domain.exception.UnauthorizedReviewAccessException
import com.mad.jellomarkserver.review.core.domain.model.*
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.UpdateReviewCommand
import com.mad.jellomarkserver.review.port.driving.UpdateReviewUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.Instant
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class UpdateReviewUseCaseImplTest {

    @Mock
    private lateinit var shopReviewPort: ShopReviewPort

    @Mock
    private lateinit var updateBeautishopStatsUseCase: UpdateBeautishopStatsUseCase

    private lateinit var useCase: UpdateReviewUseCase

    @BeforeEach
    fun setup() {
        useCase = UpdateReviewUseCaseImpl(shopReviewPort, updateBeautishopStatsUseCase)
    }

    @Test
    fun `should update review successfully when owner requests`() {
        val reviewId = ReviewId.new()
        val memberId = MemberId.new()
        val existingReview = createReview(reviewId, memberId)

        `when`(shopReviewPort.findById(any())).thenReturn(existingReview)
        `when`(shopReviewPort.save(any())).thenAnswer { invocation ->
            invocation.arguments[0] as ShopReview
        }

        val command = UpdateReviewCommand(
            reviewId = reviewId.value.toString(),
            memberId = memberId.value.toString(),
            rating = 4,
            content = "수정된 리뷰 내용입니다. 다시 방문해보니 좋았어요.",
            images = listOf("https://example.com/new-img.jpg")
        )

        val result = useCase.execute(command)

        assertEquals(4, result.rating?.value)
        assertEquals("수정된 리뷰 내용입니다. 다시 방문해보니 좋았어요.", result.content?.value)
        assertEquals(1, result.images?.urls?.size)
        verify(shopReviewPort).save(any())
    }

    @Test
    fun `should throw ReviewNotFoundException when review does not exist`() {
        val reviewId = ReviewId.new()
        val memberId = MemberId.new()

        `when`(shopReviewPort.findById(any())).thenReturn(null)

        val command = UpdateReviewCommand(
            reviewId = reviewId.value.toString(),
            memberId = memberId.value.toString(),
            rating = 4,
            content = "수정된 리뷰 내용입니다. 다시 방문해보니 좋았어요.",
            images = null
        )

        assertFailsWith<ReviewNotFoundException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw UnauthorizedReviewAccessException when non-owner tries to update`() {
        val reviewId = ReviewId.new()
        val ownerId = MemberId.new()
        val otherMemberId = MemberId.new()
        val existingReview = createReview(reviewId, ownerId)

        `when`(shopReviewPort.findById(any())).thenReturn(existingReview)

        val command = UpdateReviewCommand(
            reviewId = reviewId.value.toString(),
            memberId = otherMemberId.value.toString(),
            rating = 4,
            content = "수정된 리뷰 내용입니다. 다시 방문해보니 좋았어요.",
            images = null
        )

        assertFailsWith<UnauthorizedReviewAccessException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw InvalidReviewRatingException when rating is invalid`() {
        val reviewId = ReviewId.new()
        val memberId = MemberId.new()
        val existingReview = createReview(reviewId, memberId)

        `when`(shopReviewPort.findById(any())).thenReturn(existingReview)

        val command = UpdateReviewCommand(
            reviewId = reviewId.value.toString(),
            memberId = memberId.value.toString(),
            rating = 6,
            content = "수정된 리뷰 내용입니다. 다시 방문해보니 좋았어요.",
            images = null
        )

        assertFailsWith<InvalidReviewRatingException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw InvalidReviewContentException when content is too short`() {
        val reviewId = ReviewId.new()
        val memberId = MemberId.new()
        val existingReview = createReview(reviewId, memberId)

        `when`(shopReviewPort.findById(any())).thenReturn(existingReview)

        val command = UpdateReviewCommand(
            reviewId = reviewId.value.toString(),
            memberId = memberId.value.toString(),
            rating = 4,
            content = "짧음",
            images = null
        )

        assertFailsWith<InvalidReviewContentException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should update review without images`() {
        val reviewId = ReviewId.new()
        val memberId = MemberId.new()
        val existingReview = createReview(reviewId, memberId)

        `when`(shopReviewPort.findById(any())).thenReturn(existingReview)
        `when`(shopReviewPort.save(any())).thenAnswer { invocation ->
            invocation.arguments[0] as ShopReview
        }

        val command = UpdateReviewCommand(
            reviewId = reviewId.value.toString(),
            memberId = memberId.value.toString(),
            rating = 3,
            content = "수정된 리뷰 내용입니다. 이미지 없이 수정했습니다.",
            images = null
        )

        val result = useCase.execute(command)

        assertEquals(3, result.rating?.value)
        assertNull(result.images)
    }

    private fun createReview(reviewId: ReviewId, memberId: MemberId): ShopReview {
        return ShopReview.reconstruct(
            id = reviewId,
            shopId = ShopId.new(),
            memberId = memberId,
            rating = ReviewRating.of(5),
            content = ReviewContent.of("원본 리뷰 내용입니다. 정말 좋았어요!"),
            images = ReviewImages.of(listOf("https://example.com/img1.jpg")),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
