package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateReviewRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.UpdateReviewRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.PagedReviewsResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.ReviewResponse
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.review.port.driving.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class ReviewController(
    private val createReviewUseCase: CreateReviewUseCase,
    private val listReviewsUseCase: ListReviewsUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val memberPort: MemberPort
) {
    @PostMapping("/api/beautishops/{shopId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    fun createReview(
        @PathVariable shopId: String,
        @RequestBody request: CreateReviewRequest,
        servletRequest: HttpServletRequest
    ): ReviewResponse {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can create reviews")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = CreateReviewCommand(
            shopId = shopId,
            memberId = member.id.value.toString(),
            rating = request.rating,
            content = request.content,
            images = request.images
        )

        val review = createReviewUseCase.execute(command)
        return ReviewResponse.from(review, member.displayName.value)
    }

    @GetMapping("/api/beautishops/{shopId}/reviews")
    fun listReviews(
        @PathVariable shopId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt,desc") sort: String
    ): PagedReviewsResponse {
        val command = ListReviewsCommand(shopId = shopId, page = page, size = size, sort = sort)
        val result = listReviewsUseCase.execute(command)

        val memberIds = result.items.map { MemberId.from(it.memberId.value) }
        val members = memberPort.findByIds(memberIds)
        val memberDisplayNames = members.associate {
            it.id.value.toString() to it.displayName.value
        }

        return PagedReviewsResponse.from(result, memberDisplayNames)
    }

    @PutMapping("/api/beautishops/{shopId}/reviews/{reviewId}")
    fun updateReview(
        @PathVariable shopId: String,
        @PathVariable reviewId: String,
        @RequestBody request: UpdateReviewRequest,
        servletRequest: HttpServletRequest
    ): ReviewResponse {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can update reviews")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = UpdateReviewCommand(
            reviewId = reviewId,
            memberId = member.id.value.toString(),
            rating = request.rating,
            content = request.content,
            images = request.images
        )

        val review = updateReviewUseCase.execute(command)
        return ReviewResponse.from(review, member.displayName.value)
    }

    @DeleteMapping("/api/beautishops/{shopId}/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteReview(
        @PathVariable shopId: String,
        @PathVariable reviewId: String,
        servletRequest: HttpServletRequest
    ) {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can delete reviews")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = DeleteReviewCommand(
            reviewId = reviewId,
            memberId = member.id.value.toString()
        )

        deleteReviewUseCase.execute(command)
    }
}
