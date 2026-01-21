package com.mad.jellomarkserver.review.core.application

import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.review.port.driving.GetMemberReviewsCommand
import com.mad.jellomarkserver.review.port.driving.GetMemberReviewsUseCase
import com.mad.jellomarkserver.review.port.driving.PagedReviews
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetMemberReviewsUseCaseImpl(
    private val shopReviewPort: ShopReviewPort
) : GetMemberReviewsUseCase {

    override fun execute(command: GetMemberReviewsCommand): PagedReviews {
        val memberId = MemberId.from(UUID.fromString(command.memberId))
        val sort = parseSort(command.sort)
        val pageable = PageRequest.of(command.page, command.size, sort)
        val page = shopReviewPort.findByMemberId(memberId, pageable)

        return PagedReviews(
            items = page.content,
            hasNext = page.hasNext(),
            totalElements = page.totalElements
        )
    }

    private fun parseSort(sortString: String): Sort {
        val parts = sortString.split(",")
        if (parts.size != 2) {
            return Sort.by(Sort.Direction.DESC, "createdAt")
        }

        val property = parts[0].trim()
        val direction = when (parts[1].trim().lowercase()) {
            "asc" -> Sort.Direction.ASC
            "desc" -> Sort.Direction.DESC
            else -> Sort.Direction.DESC
        }

        val allowedProperties = setOf("createdAt", "rating")
        if (property !in allowedProperties) {
            return Sort.by(Sort.Direction.DESC, "createdAt")
        }

        return Sort.by(direction, property)
    }
}
