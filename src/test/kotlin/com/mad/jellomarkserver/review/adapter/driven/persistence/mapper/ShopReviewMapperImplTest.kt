package com.mad.jellomarkserver.review.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.adapter.driven.persistence.entity.ShopReviewJpaEntity
import com.mad.jellomarkserver.review.core.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

class ShopReviewMapperImplTest {

    private val mapper = ShopReviewMapperImpl()
    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))

    @Test
    fun `should map domain to entity`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val review = ShopReview.create(
            shopId = shopId,
            memberId = memberId,
            rating = ReviewRating.of(5),
            content = ReviewContent.of("정말 훌륭한 서비스였습니다!"),
            images = ReviewImages.of(listOf("https://example.com/img1.jpg", "https://example.com/img2.jpg")),
            clock = fixedClock
        )

        val entity = mapper.toEntity(review)

        assertEquals(review.id.value, entity.id)
        assertEquals(shopId.value, entity.shopId)
        assertEquals(memberId.value, entity.memberId)
        assertEquals(5, entity.rating)
        assertEquals("정말 훌륭한 서비스였습니다!", entity.content)
        assertEquals("https://example.com/img1.jpg,https://example.com/img2.jpg", entity.images)
        assertEquals(review.createdAt, entity.createdAt)
        assertEquals(review.updatedAt, entity.updatedAt)
    }

    @Test
    fun `should map domain to entity without images`() {
        val review = ShopReview.create(
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            rating = ReviewRating.of(4),
            content = ReviewContent.of("좋은 경험이었습니다. 감사합니다."),
            images = null,
            clock = fixedClock
        )

        val entity = mapper.toEntity(review)

        assertNull(entity.images)
    }

    @Test
    fun `should map entity to domain`() {
        val id = UUID.randomUUID()
        val shopId = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-02T00:00:00Z")

        val entity = ShopReviewJpaEntity(
            id = id,
            shopId = shopId,
            memberId = memberId,
            rating = 4,
            content = "데이터베이스에서 복원된 리뷰입니다.",
            images = "https://example.com/img1.jpg,https://example.com/img2.jpg",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val domain = mapper.toDomain(entity)

        assertEquals(id, domain.id.value)
        assertEquals(shopId, domain.shopId.value)
        assertEquals(memberId, domain.memberId.value)
        assertEquals(4, domain.rating.value)
        assertEquals("데이터베이스에서 복원된 리뷰입니다.", domain.content.value)
        assertEquals(2, domain.images?.urls?.size)
        assertEquals("https://example.com/img1.jpg", domain.images?.urls?.get(0))
        assertEquals(createdAt, domain.createdAt)
        assertEquals(updatedAt, domain.updatedAt)
    }

    @Test
    fun `should map entity to domain without images`() {
        val entity = ShopReviewJpaEntity(
            id = UUID.randomUUID(),
            shopId = UUID.randomUUID(),
            memberId = UUID.randomUUID(),
            rating = 3,
            content = "이미지 없는 리뷰 테스트입니다.",
            images = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val domain = mapper.toDomain(entity)

        assertNull(domain.images)
    }

    @Test
    fun `should handle empty images string`() {
        val entity = ShopReviewJpaEntity(
            id = UUID.randomUUID(),
            shopId = UUID.randomUUID(),
            memberId = UUID.randomUUID(),
            rating = 3,
            content = "빈 이미지 문자열 테스트입니다.",
            images = "",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val domain = mapper.toDomain(entity)

        assertNull(domain.images)
    }
}
