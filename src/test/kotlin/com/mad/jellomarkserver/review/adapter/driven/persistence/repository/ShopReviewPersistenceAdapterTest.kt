package com.mad.jellomarkserver.review.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslatorImpl
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.adapter.driven.persistence.entity.ShopReviewJpaEntity
import com.mad.jellomarkserver.review.adapter.driven.persistence.mapper.ShopReviewMapper
import com.mad.jellomarkserver.review.core.domain.exception.DuplicateReviewException
import com.mad.jellomarkserver.review.core.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class ShopReviewPersistenceAdapterTest {

    @Mock
    private lateinit var jpaRepository: ShopReviewJpaRepository

    @Mock
    private lateinit var mapper: ShopReviewMapper

    private val constraintTranslator: ConstraintViolationTranslator = ConstraintViolationTranslatorImpl()

    private lateinit var adapter: ShopReviewPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = ShopReviewPersistenceAdapter(jpaRepository, mapper, constraintTranslator)
    }

    @Test
    fun `should save review successfully`() {
        val review = createReview()
        val entity = createEntity()

        `when`(mapper.toEntity(review)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(review)

        val result = adapter.save(review)

        assertEquals(review, result)
        verify(mapper).toEntity(review)
        verify(jpaRepository).saveAndFlush(entity)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should throw DuplicateReviewException when shop_member constraint is violated`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val review = createReviewWithIds(shopId, memberId)
        val entity = createEntity()

        val exception = DataIntegrityViolationException("uk_shop_reviews_shop_member")

        `when`(mapper.toEntity(review)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertThrows(DuplicateReviewException::class.java) {
            adapter.save(review)
        }

        assertTrue(thrownException.message!!.contains(shopId.value.toString()))
        assertTrue(thrownException.message!!.contains(memberId.value.toString()))
        verify(mapper).toEntity(review)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should find review by id`() {
        val reviewId = ReviewId.new()
        val review = createReview()
        val entity = createEntity()

        `when`(jpaRepository.findById(reviewId.value)).thenReturn(Optional.of(entity))
        `when`(mapper.toDomain(entity)).thenReturn(review)

        val result = adapter.findById(reviewId)

        assertEquals(review, result)
        verify(jpaRepository).findById(reviewId.value)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should return null when review not found by id`() {
        val reviewId = ReviewId.new()

        `when`(jpaRepository.findById(reviewId.value)).thenReturn(Optional.empty())

        val result = adapter.findById(reviewId)

        assertNull(result)
        verify(jpaRepository).findById(reviewId.value)
    }

    @Test
    fun `should find reviews by shopId with pagination`() {
        val shopId = ShopId.new()
        val pageable = PageRequest.of(0, 10)
        val entity1 = createEntity()
        val entity2 = createEntity()
        val review1 = createReview()
        val review2 = createReview()
        val pageResult = PageImpl(listOf(entity1, entity2), pageable, 2)

        `when`(jpaRepository.findByShopId(shopId.value, pageable)).thenReturn(pageResult)
        `when`(mapper.toDomain(entity1)).thenReturn(review1)
        `when`(mapper.toDomain(entity2)).thenReturn(review2)

        val result = adapter.findByShopId(shopId, pageable)

        assertEquals(2, result.content.size)
        assertEquals(review1, result.content[0])
        assertEquals(review2, result.content[1])
        verify(jpaRepository).findByShopId(shopId.value, pageable)
    }

    @Test
    fun `should return empty page when no reviews found for shopId`() {
        val shopId = ShopId.new()
        val pageable = PageRequest.of(0, 10)
        val pageResult = PageImpl<ShopReviewJpaEntity>(emptyList(), pageable, 0)

        `when`(jpaRepository.findByShopId(shopId.value, pageable)).thenReturn(pageResult)

        val result = adapter.findByShopId(shopId, pageable)

        assertTrue(result.content.isEmpty())
        verify(jpaRepository).findByShopId(shopId.value, pageable)
    }

    @Test
    fun `should find reviews by memberId`() {
        val memberId = MemberId.new()
        val entity1 = createEntity()
        val entity2 = createEntity()
        val review1 = createReview()
        val review2 = createReview()

        `when`(jpaRepository.findByMemberId(memberId.value)).thenReturn(listOf(entity1, entity2))
        `when`(mapper.toDomain(entity1)).thenReturn(review1)
        `when`(mapper.toDomain(entity2)).thenReturn(review2)

        val result = adapter.findByMemberId(memberId)

        assertEquals(2, result.size)
        assertEquals(review1, result[0])
        assertEquals(review2, result[1])
        verify(jpaRepository).findByMemberId(memberId.value)
    }

    @Test
    fun `should return empty list when no reviews found for memberId`() {
        val memberId = MemberId.new()

        `when`(jpaRepository.findByMemberId(memberId.value)).thenReturn(emptyList())

        val result = adapter.findByMemberId(memberId)

        assertTrue(result.isEmpty())
        verify(jpaRepository).findByMemberId(memberId.value)
    }

    @Test
    fun `should return true when review exists by shopId and memberId`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()

        `when`(jpaRepository.existsByShopIdAndMemberId(shopId.value, memberId.value)).thenReturn(true)

        val result = adapter.existsByShopIdAndMemberId(shopId, memberId)

        assertTrue(result)
        verify(jpaRepository).existsByShopIdAndMemberId(shopId.value, memberId.value)
    }

    @Test
    fun `should return false when review does not exist by shopId and memberId`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()

        `when`(jpaRepository.existsByShopIdAndMemberId(shopId.value, memberId.value)).thenReturn(false)

        val result = adapter.existsByShopIdAndMemberId(shopId, memberId)

        assertFalse(result)
        verify(jpaRepository).existsByShopIdAndMemberId(shopId.value, memberId.value)
    }

    @Test
    fun `should delete review by id`() {
        val reviewId = ReviewId.new()

        adapter.delete(reviewId)

        verify(jpaRepository).deleteById(reviewId.value)
    }

    private fun createReview(): ShopReview {
        return ShopReview.reconstruct(
            id = ReviewId.new(),
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            rating = ReviewRating.of(5),
            content = ReviewContent.of("정말 훌륭한 서비스였습니다!"),
            images = ReviewImages.of(listOf("https://example.com/img1.jpg")),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    private fun createReviewWithIds(shopId: ShopId, memberId: MemberId): ShopReview {
        return ShopReview.reconstruct(
            id = ReviewId.new(),
            shopId = shopId,
            memberId = memberId,
            rating = ReviewRating.of(5),
            content = ReviewContent.of("정말 훌륭한 서비스였습니다!"),
            images = ReviewImages.of(listOf("https://example.com/img1.jpg")),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    private fun createEntity(): ShopReviewJpaEntity {
        return ShopReviewJpaEntity(
            id = UUID.randomUUID(),
            shopId = UUID.randomUUID(),
            memberId = UUID.randomUUID(),
            rating = 5,
            content = "정말 훌륭한 서비스였습니다!",
            images = "https://example.com/img1.jpg",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
