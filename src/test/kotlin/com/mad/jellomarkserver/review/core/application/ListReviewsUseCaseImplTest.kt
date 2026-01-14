package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.core.domain.model.ReviewContent
import com.mad.jellomarkserver.review.core.domain.model.ReviewId
import com.mad.jellomarkserver.review.core.domain.model.ReviewRating
import com.mad.jellomarkserver.review.core.domain.model.ShopReview
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.ListReviewsCommand
import com.mad.jellomarkserver.review.port.driving.ListReviewsUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ListReviewsUseCaseImplTest {

    @Mock
    private lateinit var shopReviewPort: ShopReviewPort

    private lateinit var useCase: ListReviewsUseCase

    @BeforeEach
    fun setup() {
        useCase = ListReviewsUseCaseImpl(shopReviewPort)
    }

    @Test
    fun `should return paged reviews for a shop`() {
        val shopId = ShopId.new()
        val review1 = createReview(shopId)
        val review2 = createReview(shopId)
        val pageable = PageRequest.of(0, 20)
        val pageResult = PageImpl(listOf(review1, review2), pageable, 2)

        `when`(
            shopReviewPort.findByShopId(
                ArgumentMatchers.any() ?: shopId,
                ArgumentMatchers.any() ?: pageable
            )
        ).thenReturn(pageResult)

        val command = ListReviewsCommand(shopId = shopId.value.toString())
        val result = useCase.execute(command)

        assertEquals(2, result.items.size)
        assertEquals(review1, result.items[0])
        assertEquals(review2, result.items[1])
        assertFalse(result.hasNext)
        assertEquals(2, result.totalElements)
    }

    @Test
    fun `should return empty page when no reviews exist`() {
        val shopId = ShopId.new()
        val pageable = PageRequest.of(0, 20)
        val pageResult = PageImpl<ShopReview>(emptyList(), pageable, 0)

        `when`(
            shopReviewPort.findByShopId(
                ArgumentMatchers.any() ?: shopId,
                ArgumentMatchers.any() ?: pageable
            )
        ).thenReturn(pageResult)

        val command = ListReviewsCommand(shopId = shopId.value.toString())
        val result = useCase.execute(command)

        assertTrue(result.items.isEmpty())
        assertFalse(result.hasNext)
        assertEquals(0, result.totalElements)
    }

    @Test
    fun `should return hasNext true when more pages exist`() {
        val shopId = ShopId.new()
        val reviews = (1..10).map { createReview(shopId) }
        val pageable = PageRequest.of(0, 10)
        val pageResult = PageImpl(reviews, pageable, 25)

        `when`(
            shopReviewPort.findByShopId(
                ArgumentMatchers.any() ?: shopId,
                ArgumentMatchers.any() ?: pageable
            )
        ).thenReturn(pageResult)

        val command = ListReviewsCommand(shopId = shopId.value.toString(), page = 0, size = 10)
        val result = useCase.execute(command)

        assertEquals(10, result.items.size)
        assertTrue(result.hasNext)
        assertEquals(25, result.totalElements)
    }

    @Test
    fun `should use custom page and size`() {
        val shopId = ShopId.new()
        val review = createReview(shopId)
        val pageable = PageRequest.of(2, 5)
        val pageResult = PageImpl(listOf(review), pageable, 15)

        `when`(
            shopReviewPort.findByShopId(
                ArgumentMatchers.any() ?: shopId,
                ArgumentMatchers.any() ?: pageable
            )
        ).thenReturn(pageResult)

        val command = ListReviewsCommand(shopId = shopId.value.toString(), page = 2, size = 5)
        val result = useCase.execute(command)

        assertEquals(1, result.items.size)
        assertEquals(15, result.totalElements)
    }

    @Test
    fun `should use default sort when sort parameter is not specified`() {
        val shopId = ShopId.new()
        val review = createReview(shopId)
        val pageableCaptor = argumentCaptor<Pageable>()

        `when`(
            shopReviewPort.findByShopId(
                ArgumentMatchers.any() ?: shopId,
                pageableCaptor.capture()
            )
        ).thenReturn(PageImpl(listOf(review), PageRequest.of(0, 20), 1))

        val command = ListReviewsCommand(shopId = shopId.value.toString())
        useCase.execute(command)

        val capturedPageable = pageableCaptor.firstValue
        val sort = capturedPageable.sort
        assertTrue(sort.isSorted)
        assertEquals("createdAt", sort.getOrderFor("createdAt")?.property)
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("createdAt")?.direction)
    }

    @Test
    fun `should sort by rating descending when specified`() {
        val shopId = ShopId.new()
        val review = createReview(shopId)
        val pageableCaptor = argumentCaptor<Pageable>()

        `when`(
            shopReviewPort.findByShopId(
                ArgumentMatchers.any() ?: shopId,
                pageableCaptor.capture()
            )
        ).thenReturn(PageImpl(listOf(review), PageRequest.of(0, 20), 1))

        val command = ListReviewsCommand(
            shopId = shopId.value.toString(),
            sort = "rating,desc"
        )
        useCase.execute(command)

        val capturedPageable = pageableCaptor.firstValue
        val sort = capturedPageable.sort
        assertTrue(sort.isSorted)
        assertEquals("rating", sort.getOrderFor("rating")?.property)
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("rating")?.direction)
    }

    @Test
    fun `should sort by rating ascending when specified`() {
        val shopId = ShopId.new()
        val review = createReview(shopId)
        val pageableCaptor = argumentCaptor<Pageable>()

        `when`(
            shopReviewPort.findByShopId(
                ArgumentMatchers.any() ?: shopId,
                pageableCaptor.capture()
            )
        ).thenReturn(PageImpl(listOf(review), PageRequest.of(0, 20), 1))

        val command = ListReviewsCommand(
            shopId = shopId.value.toString(),
            sort = "rating,asc"
        )
        useCase.execute(command)

        val capturedPageable = pageableCaptor.firstValue
        val sort = capturedPageable.sort
        assertTrue(sort.isSorted)
        assertEquals("rating", sort.getOrderFor("rating")?.property)
        assertEquals(Sort.Direction.ASC, sort.getOrderFor("rating")?.direction)
    }

    @Test
    fun `should fallback to default sort when invalid property is specified`() {
        val shopId = ShopId.new()
        val review = createReview(shopId)
        val pageableCaptor = argumentCaptor<Pageable>()

        `when`(
            shopReviewPort.findByShopId(
                ArgumentMatchers.any() ?: shopId,
                pageableCaptor.capture()
            )
        ).thenReturn(PageImpl(listOf(review), PageRequest.of(0, 20), 1))

        val command = ListReviewsCommand(
            shopId = shopId.value.toString(),
            sort = "invalidProperty,desc"
        )
        useCase.execute(command)

        val capturedPageable = pageableCaptor.firstValue
        val sort = capturedPageable.sort
        assertTrue(sort.isSorted)
        assertEquals("createdAt", sort.getOrderFor("createdAt")?.property)
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("createdAt")?.direction)
    }

    @Test
    fun `should fallback to default sort when malformed sort string is provided`() {
        val shopId = ShopId.new()
        val review = createReview(shopId)
        val pageableCaptor = argumentCaptor<Pageable>()

        `when`(
            shopReviewPort.findByShopId(
                ArgumentMatchers.any() ?: shopId,
                pageableCaptor.capture()
            )
        ).thenReturn(PageImpl(listOf(review), PageRequest.of(0, 20), 1))

        val command = ListReviewsCommand(
            shopId = shopId.value.toString(),
            sort = "invalidformat"
        )
        useCase.execute(command)

        val capturedPageable = pageableCaptor.firstValue
        val sort = capturedPageable.sort
        assertTrue(sort.isSorted)
        assertEquals("createdAt", sort.getOrderFor("createdAt")?.property)
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("createdAt")?.direction)
    }

    private fun createReview(shopId: ShopId): ShopReview {
        return ShopReview.reconstruct(
            id = ReviewId.new(),
            shopId = shopId,
            memberId = MemberId.new(),
            rating = ReviewRating.of(5),
            content = ReviewContent.of("정말 훌륭한 서비스였습니다!"),
            images = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
