package com.mad.jellomarkserver.review.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class ShopReviewTest {

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))

    @Test
    fun `should create shop review with valid data`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val rating = ReviewRating.of(5)
        val content = ReviewContent.of("정말 친절하고 시술도 꼼꼼해요!")
        val images = ReviewImages.of(listOf("https://example.com/image1.jpg"))

        val review = ShopReview.create(
            shopId = shopId,
            memberId = memberId,
            rating = rating,
            content = content,
            images = images,
            clock = fixedClock
        )

        assertNotNull(review.id)
        assertEquals(shopId, review.shopId)
        assertEquals(memberId, review.memberId)
        assertEquals(5, review.rating?.value)
        assertEquals("정말 친절하고 시술도 꼼꼼해요!", review.content?.value)
        assertEquals(1, review.images?.urls?.size)
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), review.createdAt)
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), review.updatedAt)
    }

    @Test
    fun `should create shop review without images`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val rating = ReviewRating.of(4)
        val content = ReviewContent.of("좋은 서비스였습니다. 추천합니다!")

        val review = ShopReview.create(
            shopId = shopId,
            memberId = memberId,
            rating = rating,
            content = content,
            images = null,
            clock = fixedClock
        )

        assertNull(review.images)
    }

    @Test
    fun `should update review rating and content`() {
        val review = createTestReview()
        val updateClock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneId.of("UTC"))

        val updatedReview = review.update(
            rating = ReviewRating.of(3),
            content = ReviewContent.of("수정된 리뷰 내용입니다. 다시 방문했어요."),
            images = null,
            clock = updateClock
        )

        assertEquals(review.id, updatedReview.id)
        assertEquals(review.shopId, updatedReview.shopId)
        assertEquals(review.memberId, updatedReview.memberId)
        assertEquals(3, updatedReview.rating?.value)
        assertEquals("수정된 리뷰 내용입니다. 다시 방문했어요.", updatedReview.content?.value)
        assertEquals(review.createdAt, updatedReview.createdAt)
        assertEquals(Instant.parse("2025-01-02T00:00:00Z"), updatedReview.updatedAt)
    }

    @Test
    fun `should return true when member owns the review`() {
        val memberId = MemberId.new()
        val review = ShopReview.create(
            shopId = ShopId.new(),
            memberId = memberId,
            rating = ReviewRating.of(5),
            content = ReviewContent.of("테스트 리뷰 내용입니다."),
            images = null,
            clock = fixedClock
        )

        assertTrue(review.isOwnedBy(memberId))
    }

    @Test
    fun `should return false when member does not own the review`() {
        val review = createTestReview()
        val otherMemberId = MemberId.new()

        assertFalse(review.isOwnedBy(otherMemberId))
    }

    @Test
    fun `should reconstruct review from persisted data`() {
        val id = ReviewId.new()
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val rating = ReviewRating.of(4)
        val content = ReviewContent.of("데이터베이스에서 복원된 리뷰입니다.")
        val images = ReviewImages.of(listOf("https://example.com/img1.jpg", "https://example.com/img2.jpg"))
        val createdAt = Instant.parse("2024-12-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-12-15T00:00:00Z")

        val review = ShopReview.reconstruct(
            id = id,
            shopId = shopId,
            memberId = memberId,
            rating = rating,
            content = content,
            images = images,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, review.id)
        assertEquals(shopId, review.shopId)
        assertEquals(memberId, review.memberId)
        assertEquals(4, review.rating?.value)
        assertEquals(createdAt, review.createdAt)
        assertEquals(updatedAt, review.updatedAt)
    }

    private fun createTestReview(): ShopReview {
        return ShopReview.create(
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            rating = ReviewRating.of(5),
            content = ReviewContent.of("테스트 리뷰 내용입니다."),
            images = null,
            clock = fixedClock
        )
    }
}
