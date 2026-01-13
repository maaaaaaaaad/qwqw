package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsCommand
import com.mad.jellomarkserver.review.port.driven.ReviewStats
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class UpdateBeautishopStatsUseCaseImplTest {

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var shopReviewPort: ShopReviewPort

    private lateinit var useCase: UpdateBeautishopStatsUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = UpdateBeautishopStatsUseCaseImpl(beautishopPort, shopReviewPort)
    }

    @Test
    fun `should update beautishop stats with calculated review stats`() {
        val shopIdString = UUID.randomUUID().toString()
        val reviewStats = ReviewStats(averageRating = 4.5, reviewCount = 10)

        whenever(shopReviewPort.calculateStats(any())).thenReturn(reviewStats)

        val command = UpdateBeautishopStatsCommand(shopId = shopIdString)
        useCase.execute(command)

        verify(shopReviewPort).calculateStats(any())
        verify(beautishopPort).updateStats(any(), eq(4.5), eq(10))
    }

    @Test
    fun `should update beautishop stats to zero when no reviews exist`() {
        val shopIdString = UUID.randomUUID().toString()
        val reviewStats = ReviewStats(averageRating = 0.0, reviewCount = 0)

        whenever(shopReviewPort.calculateStats(any())).thenReturn(reviewStats)

        val command = UpdateBeautishopStatsCommand(shopId = shopIdString)
        useCase.execute(command)

        verify(beautishopPort).updateStats(any(), eq(0.0), eq(0))
    }
}
