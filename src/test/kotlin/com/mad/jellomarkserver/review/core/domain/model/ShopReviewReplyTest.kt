package com.mad.jellomarkserver.review.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class ShopReviewReplyTest {

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))
    private val replyClock = Clock.fixed(Instant.parse("2025-01-05T00:00:00Z"), ZoneId.of("UTC"))

    @Test
    fun `should add reply to review`() {
        val review = createTestReview()
        val replyContent = ReplyContent.of("감사합니다! 또 방문해주세요.")

        val replied = review.reply(replyContent, replyClock)

        assertEquals("감사합니다! 또 방문해주세요.", replied.ownerReplyContent?.value)
        assertEquals(Instant.parse("2025-01-05T00:00:00Z"), replied.ownerReplyCreatedAt)
    }

    @Test
    fun `should preserve original review fields after reply`() {
        val review = createTestReview()
        val replyContent = ReplyContent.of("감사합니다! 또 방문해주세요.")

        val replied = review.reply(replyContent, replyClock)

        assertEquals(review.id, replied.id)
        assertEquals(review.shopId, replied.shopId)
        assertEquals(review.memberId, replied.memberId)
        assertEquals(review.rating, replied.rating)
        assertEquals(review.content, replied.content)
        assertEquals(review.images, replied.images)
        assertEquals(review.createdAt, replied.createdAt)
        assertEquals(review.updatedAt, replied.updatedAt)
    }

    @Test
    fun `should overwrite existing reply`() {
        val review = createTestReview()
        val firstReply = ReplyContent.of("첫 번째 답글입니다.")
        val secondReply = ReplyContent.of("수정된 답글입니다.")
        val laterClock = Clock.fixed(Instant.parse("2025-01-10T00:00:00Z"), ZoneId.of("UTC"))

        val replied = review.reply(firstReply, replyClock).reply(secondReply, laterClock)

        assertEquals("수정된 답글입니다.", replied.ownerReplyContent?.value)
        assertEquals(Instant.parse("2025-01-10T00:00:00Z"), replied.ownerReplyCreatedAt)
    }

    @Test
    fun `should delete reply`() {
        val review = createTestReview()
        val replyContent = ReplyContent.of("감사합니다!")
        val replied = review.reply(replyContent, replyClock)

        val deleted = replied.deleteReply()

        assertNull(deleted.ownerReplyContent)
        assertNull(deleted.ownerReplyCreatedAt)
    }

    @Test
    fun `should return hasReply true when reply exists`() {
        val review = createTestReview()
        val replied = review.reply(ReplyContent.of("감사합니다!"), replyClock)

        assertTrue(replied.hasReply())
    }

    @Test
    fun `should return hasReply false when no reply`() {
        val review = createTestReview()

        assertFalse(review.hasReply())
    }

    @Test
    fun `should return hasReply false after reply deleted`() {
        val review = createTestReview()
        val replied = review.reply(ReplyContent.of("감사합니다!"), replyClock)
        val deleted = replied.deleteReply()

        assertFalse(deleted.hasReply())
    }

    @Test
    fun `should reconstruct review with reply fields`() {
        val id = ReviewId.new()
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val replyContent = ReplyContent.of("감사합니다!")
        val replyCreatedAt = Instant.parse("2025-01-05T00:00:00Z")

        val review = ShopReview.reconstruct(
            id = id,
            shopId = shopId,
            memberId = memberId,
            rating = ReviewRating.of(5),
            content = ReviewContent.of("좋은 서비스였습니다. 추천합니다!"),
            images = null,
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
            ownerReplyContent = replyContent,
            ownerReplyCreatedAt = replyCreatedAt
        )

        assertEquals("감사합니다!", review.ownerReplyContent?.value)
        assertEquals(replyCreatedAt, review.ownerReplyCreatedAt)
        assertTrue(review.hasReply())
    }

    @Test
    fun `should reconstruct review without reply fields`() {
        val review = ShopReview.reconstruct(
            id = ReviewId.new(),
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            rating = ReviewRating.of(5),
            content = ReviewContent.of("좋은 서비스였습니다. 추천합니다!"),
            images = null,
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        )

        assertNull(review.ownerReplyContent)
        assertNull(review.ownerReplyCreatedAt)
        assertFalse(review.hasReply())
    }

    private fun createTestReview(): ShopReview {
        return ShopReview.create(
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            rating = ReviewRating.of(5),
            content = ReviewContent.of("정말 좋은 서비스였습니다."),
            images = null,
            clock = fixedClock
        )
    }
}
