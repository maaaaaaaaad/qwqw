package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.ListReviewsCommand
import com.mad.jellomarkserver.review.port.driving.ListReviewsUseCase
import com.mad.jellomarkserver.review.port.driving.PagedReviews
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class ListReviewsUseCaseImpl(
    private val shopReviewPort: ShopReviewPort
) : ListReviewsUseCase {

    override fun execute(command: ListReviewsCommand): PagedReviews {
        val shopId = ShopId.from(UUID.fromString(command.shopId))
        val pageable = PageRequest.of(command.page, command.size)
        val page = shopReviewPort.findByShopId(shopId, pageable)

        return PagedReviews(
            items = page.content,
            hasNext = page.hasNext(),
            totalElements = page.totalElements
        )
    }
}
