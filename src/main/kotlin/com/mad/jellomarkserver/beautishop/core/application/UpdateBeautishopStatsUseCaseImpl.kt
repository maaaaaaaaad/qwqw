package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsCommand
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopStatsUseCase
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UpdateBeautishopStatsUseCaseImpl(
    private val beautishopPort: BeautishopPort,
    private val shopReviewPort: ShopReviewPort
) : UpdateBeautishopStatsUseCase {

    @Transactional
    override fun execute(command: UpdateBeautishopStatsCommand) {
        val shopId = ShopId.from(UUID.fromString(command.shopId))
        val stats = shopReviewPort.calculateStats(shopId)
        beautishopPort.updateStats(shopId, stats.averageRating, stats.reviewCount)
    }
}
