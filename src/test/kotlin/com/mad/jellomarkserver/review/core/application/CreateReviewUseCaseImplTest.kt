package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsUseCase
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.core.domain.exception.DuplicateReviewException
import com.mad.jellomarkserver.review.core.domain.exception.InvalidReviewContentException
import com.mad.jellomarkserver.review.core.domain.exception.InvalidReviewImagesException
import com.mad.jellomarkserver.review.core.domain.exception.InvalidReviewRatingException
import com.mad.jellomarkserver.review.core.domain.model.ShopReview
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.CreateReviewCommand
import com.mad.jellomarkserver.review.port.driving.CreateReviewUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class CreateReviewUseCaseImplTest {

    @Mock
    private lateinit var shopReviewPort: ShopReviewPort

    @Mock
    private lateinit var updateBeautishopStatsUseCase: UpdateBeautishopStatsUseCase

    private lateinit var useCase: CreateReviewUseCase

    @BeforeEach
    fun setup() {
        useCase = CreateReviewUseCaseImpl(shopReviewPort, updateBeautishopStatsUseCase)
    }

    @Test
    fun `should create review successfully with all fields`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val command = CreateReviewCommand(
            shopId = shopId.value.toString(),
            memberId = memberId.value.toString(),
            rating = 5,
            content = "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.",
            images = listOf("https://example.com/img1.jpg", "https://example.com/img2.jpg")
        )

        `when`(
            shopReviewPort.existsByShopIdAndMemberId(
                ArgumentMatchers.any() ?: shopId,
                ArgumentMatchers.any() ?: memberId
            )
        ).thenReturn(false)

        `when`(
            shopReviewPort.save(
                ArgumentMatchers.any() ?: createDummyReview()
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as ShopReview
        }

        val result = useCase.execute(command)

        assertNotNull(result)
        assertNotNull(result.id)
        assertEquals(shopId.value, result.shopId.value)
        assertEquals(memberId.value, result.memberId.value)
        assertEquals(5, result.rating.value)
        assertEquals("정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.", result.content.value)
        assertEquals(2, result.images?.urls?.size)
        assertNotNull(result.createdAt)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `should create review successfully without optional images`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val command = CreateReviewCommand(
            shopId = shopId.value.toString(),
            memberId = memberId.value.toString(),
            rating = 4,
            content = "좋은 경험이었습니다. 친절하게 응대해주셨어요.",
            images = null
        )

        `when`(
            shopReviewPort.existsByShopIdAndMemberId(
                ArgumentMatchers.any() ?: shopId,
                ArgumentMatchers.any() ?: memberId
            )
        ).thenReturn(false)

        `when`(
            shopReviewPort.save(
                ArgumentMatchers.any() ?: createDummyReview()
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as ShopReview
        }

        val result = useCase.execute(command)

        assertNotNull(result)
        assertNull(result.images)
    }

    @Test
    fun `should create review successfully with empty images list`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val command = CreateReviewCommand(
            shopId = shopId.value.toString(),
            memberId = memberId.value.toString(),
            rating = 4,
            content = "좋은 경험이었습니다. 친절하게 응대해주셨어요.",
            images = emptyList()
        )

        `when`(
            shopReviewPort.existsByShopIdAndMemberId(
                ArgumentMatchers.any() ?: shopId,
                ArgumentMatchers.any() ?: memberId
            )
        ).thenReturn(false)

        `when`(
            shopReviewPort.save(
                ArgumentMatchers.any() ?: createDummyReview()
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as ShopReview
        }

        val result = useCase.execute(command)

        assertNotNull(result)
        assertNull(result.images)
    }

    @Test
    fun `should throw InvalidReviewRatingException when rating is below minimum`() {
        val command = CreateReviewCommand(
            shopId = ShopId.new().value.toString(),
            memberId = MemberId.new().value.toString(),
            rating = 0,
            content = "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.",
            images = null
        )

        assertFailsWith<InvalidReviewRatingException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw InvalidReviewRatingException when rating is above maximum`() {
        val command = CreateReviewCommand(
            shopId = ShopId.new().value.toString(),
            memberId = MemberId.new().value.toString(),
            rating = 6,
            content = "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.",
            images = null
        )

        assertFailsWith<InvalidReviewRatingException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw InvalidReviewContentException when content is too short`() {
        val command = CreateReviewCommand(
            shopId = ShopId.new().value.toString(),
            memberId = MemberId.new().value.toString(),
            rating = 5,
            content = "좋아요",
            images = null
        )

        assertFailsWith<InvalidReviewContentException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw InvalidReviewContentException when content is too long`() {
        val command = CreateReviewCommand(
            shopId = ShopId.new().value.toString(),
            memberId = MemberId.new().value.toString(),
            rating = 5,
            content = "a".repeat(501),
            images = null
        )

        assertFailsWith<InvalidReviewContentException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw InvalidReviewImagesException when too many images`() {
        val command = CreateReviewCommand(
            shopId = ShopId.new().value.toString(),
            memberId = MemberId.new().value.toString(),
            rating = 5,
            content = "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.",
            images = listOf(
                "https://example.com/img1.jpg",
                "https://example.com/img2.jpg",
                "https://example.com/img3.jpg",
                "https://example.com/img4.jpg",
                "https://example.com/img5.jpg",
                "https://example.com/img6.jpg"
            )
        )

        assertFailsWith<InvalidReviewImagesException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw DuplicateReviewException when member already reviewed the shop`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val command = CreateReviewCommand(
            shopId = shopId.value.toString(),
            memberId = memberId.value.toString(),
            rating = 5,
            content = "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.",
            images = null
        )

        `when`(
            shopReviewPort.existsByShopIdAndMemberId(
                ArgumentMatchers.any() ?: shopId,
                ArgumentMatchers.any() ?: memberId
            )
        ).thenReturn(true)

        val exception = assertFailsWith<DuplicateReviewException> {
            useCase.execute(command)
        }

        assertTrue(exception.message!!.contains(shopId.value.toString()))
        assertTrue(exception.message!!.contains(memberId.value.toString()))
    }

    private fun createDummyReview(): ShopReview {
        return ShopReview.create(
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            rating = com.mad.jellomarkserver.review.core.domain.model.ReviewRating.of(5),
            content = com.mad.jellomarkserver.review.core.domain.model.ReviewContent.of("정말 훌륭한 서비스였습니다!"),
            images = null
        )
    }
}
