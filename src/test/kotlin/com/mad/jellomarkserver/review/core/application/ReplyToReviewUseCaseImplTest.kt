package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.*
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.review.core.domain.exception.InvalidReplyContentException
import com.mad.jellomarkserver.review.core.domain.exception.ReviewNotFoundException
import com.mad.jellomarkserver.review.core.domain.exception.UnauthorizedReviewAccessException
import com.mad.jellomarkserver.review.core.domain.model.*
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.ReplyToReviewCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class ReplyToReviewUseCaseImplTest {

    @Mock
    private lateinit var ownerPort: OwnerPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var shopReviewPort: ShopReviewPort

    private lateinit var useCase: ReplyToReviewUseCaseImpl

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))

    private val ownerId = OwnerId.new()
    private val shopId = ShopId.new()
    private val reviewId = ReviewId.new()
    private val memberId = MemberId.new()
    private val ownerEmail = "owner@test.com"

    @BeforeEach
    fun setup() {
        useCase = ReplyToReviewUseCaseImpl(ownerPort, beautishopPort, shopReviewPort)
    }

    @Test
    fun `should reply to review successfully`() {
        val owner = createTestOwner()
        val review = createTestReview()

        whenever(ownerPort.findByEmail(OwnerEmail.of(ownerEmail))).thenReturn(owner)
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)
        whenever(shopReviewPort.findById(reviewId)).thenReturn(review)
        whenever(shopReviewPort.save(any())).thenAnswer { it.arguments[0] }

        val command = ReplyToReviewCommand(
            shopId = shopId.value.toString(),
            reviewId = reviewId.value.toString(),
            ownerEmail = ownerEmail,
            content = "감사합니다! 또 방문해주세요."
        )

        useCase.execute(command)

        verify(shopReviewPort).save(any())
    }

    @Test
    fun `should throw OwnerNotFoundException when owner not found`() {
        whenever(ownerPort.findByEmail(OwnerEmail.of(ownerEmail))).thenReturn(null)

        val command = ReplyToReviewCommand(
            shopId = shopId.value.toString(),
            reviewId = reviewId.value.toString(),
            ownerEmail = ownerEmail,
            content = "감사합니다!"
        )

        assertFailsWith<OwnerNotFoundException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw UnauthorizedReviewAccessException when owner does not own shop`() {
        val owner = createTestOwner()
        val otherOwnerId = OwnerId.new()

        whenever(ownerPort.findByEmail(OwnerEmail.of(ownerEmail))).thenReturn(owner)
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(otherOwnerId)

        val command = ReplyToReviewCommand(
            shopId = shopId.value.toString(),
            reviewId = reviewId.value.toString(),
            ownerEmail = ownerEmail,
            content = "감사합니다!"
        )

        assertFailsWith<UnauthorizedReviewAccessException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw ReviewNotFoundException when review not found`() {
        val owner = createTestOwner()

        whenever(ownerPort.findByEmail(OwnerEmail.of(ownerEmail))).thenReturn(owner)
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)
        whenever(shopReviewPort.findById(reviewId)).thenReturn(null)

        val command = ReplyToReviewCommand(
            shopId = shopId.value.toString(),
            reviewId = reviewId.value.toString(),
            ownerEmail = ownerEmail,
            content = "감사합니다!"
        )

        assertFailsWith<ReviewNotFoundException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw InvalidReplyContentException for empty content`() {
        val owner = createTestOwner()
        val review = createTestReview()

        whenever(ownerPort.findByEmail(OwnerEmail.of(ownerEmail))).thenReturn(owner)
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)
        whenever(shopReviewPort.findById(reviewId)).thenReturn(review)

        val command = ReplyToReviewCommand(
            shopId = shopId.value.toString(),
            reviewId = reviewId.value.toString(),
            ownerEmail = ownerEmail,
            content = "   "
        )

        assertFailsWith<InvalidReplyContentException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw UnauthorizedReviewAccessException when shop not found`() {
        val owner = createTestOwner()

        whenever(ownerPort.findByEmail(OwnerEmail.of(ownerEmail))).thenReturn(owner)
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(null)

        val command = ReplyToReviewCommand(
            shopId = shopId.value.toString(),
            reviewId = reviewId.value.toString(),
            ownerEmail = ownerEmail,
            content = "감사합니다!"
        )

        assertFailsWith<UnauthorizedReviewAccessException> {
            useCase.execute(command)
        }
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

    private fun createTestReview(): ShopReview {
        return ShopReview.reconstruct(
            id = reviewId,
            shopId = shopId,
            memberId = memberId,
            rating = ReviewRating.of(5),
            content = ReviewContent.of("좋은 서비스였습니다. 추천합니다!"),
            images = null,
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        )
    }
}
