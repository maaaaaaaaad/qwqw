package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.*
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.review.core.domain.exception.ReviewNotFoundException
import com.mad.jellomarkserver.review.core.domain.exception.UnauthorizedReviewAccessException
import com.mad.jellomarkserver.review.core.domain.model.*
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.DeleteReviewReplyCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class DeleteReviewReplyUseCaseImplTest {

    @Mock
    private lateinit var ownerPort: OwnerPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var shopReviewPort: ShopReviewPort

    private lateinit var useCase: DeleteReviewReplyUseCaseImpl

    private val ownerId = OwnerId.new()
    private val shopId = ShopId.new()
    private val reviewId = ReviewId.new()
    private val memberId = MemberId.new()
    private val ownerEmail = "owner@test.com"

    @BeforeEach
    fun setup() {
        useCase = DeleteReviewReplyUseCaseImpl(ownerPort, beautishopPort, shopReviewPort)
    }

    @Test
    fun `should delete review reply successfully`() {
        val owner = createTestOwner()
        val review = createTestReviewWithReply()

        whenever(ownerPort.findByEmail(OwnerEmail.of(ownerEmail))).thenReturn(owner)
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)
        whenever(shopReviewPort.findById(reviewId)).thenReturn(review)
        whenever(shopReviewPort.save(any())).thenAnswer { it.arguments[0] }

        val command = DeleteReviewReplyCommand(
            shopId = shopId.value.toString(),
            reviewId = reviewId.value.toString(),
            ownerEmail = ownerEmail
        )

        useCase.execute(command)

        verify(shopReviewPort).save(any())
    }

    @Test
    fun `should throw OwnerNotFoundException when owner not found`() {
        whenever(ownerPort.findByEmail(OwnerEmail.of(ownerEmail))).thenReturn(null)

        val command = DeleteReviewReplyCommand(
            shopId = shopId.value.toString(),
            reviewId = reviewId.value.toString(),
            ownerEmail = ownerEmail
        )

        assertFailsWith<OwnerNotFoundException> {
            useCase.execute(command)
        }

        verify(shopReviewPort, never()).save(any())
    }

    @Test
    fun `should throw UnauthorizedReviewAccessException when owner does not own shop`() {
        val owner = createTestOwner()
        val otherOwnerId = OwnerId.new()

        whenever(ownerPort.findByEmail(OwnerEmail.of(ownerEmail))).thenReturn(owner)
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(otherOwnerId)

        val command = DeleteReviewReplyCommand(
            shopId = shopId.value.toString(),
            reviewId = reviewId.value.toString(),
            ownerEmail = ownerEmail
        )

        assertFailsWith<UnauthorizedReviewAccessException> {
            useCase.execute(command)
        }

        verify(shopReviewPort, never()).save(any())
    }

    @Test
    fun `should throw ReviewNotFoundException when review not found`() {
        val owner = createTestOwner()

        whenever(ownerPort.findByEmail(OwnerEmail.of(ownerEmail))).thenReturn(owner)
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)
        whenever(shopReviewPort.findById(reviewId)).thenReturn(null)

        val command = DeleteReviewReplyCommand(
            shopId = shopId.value.toString(),
            reviewId = reviewId.value.toString(),
            ownerEmail = ownerEmail
        )

        assertFailsWith<ReviewNotFoundException> {
            useCase.execute(command)
        }

        verify(shopReviewPort, never()).save(any())
    }

    private fun createTestOwner(): Owner {
        return Owner.reconstruct(
            id = ownerId,
            businessNumber = BusinessNumber.of("123-45-67890"),
            ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678"),
            ownerNickname = OwnerNickname.of("테스트사장"),
            ownerEmail = OwnerEmail.of(ownerEmail),
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        )
    }

    private fun createTestReviewWithReply(): ShopReview {
        return ShopReview.reconstruct(
            id = reviewId,
            shopId = shopId,
            memberId = memberId,
            rating = ReviewRating.of(5),
            content = ReviewContent.of("좋은 서비스였습니다. 추천합니다!"),
            images = null,
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
            ownerReplyContent = ReplyContent.of("감사합니다!"),
            ownerReplyCreatedAt = Instant.parse("2025-01-02T00:00:00Z")
        )
    }
}
