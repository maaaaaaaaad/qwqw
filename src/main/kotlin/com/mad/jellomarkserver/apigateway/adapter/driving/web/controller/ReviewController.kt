package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateReviewRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.UpdateReviewRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.PagedReviewsResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.ReviewResponse
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.review.port.driving.CreateReviewCommand
import com.mad.jellomarkserver.review.port.driving.CreateReviewUseCase
import com.mad.jellomarkserver.review.port.driving.DeleteReviewCommand
import com.mad.jellomarkserver.review.port.driving.DeleteReviewUseCase
import com.mad.jellomarkserver.review.port.driving.ListReviewsCommand
import com.mad.jellomarkserver.review.port.driving.ListReviewsUseCase
import com.mad.jellomarkserver.review.port.driving.UpdateReviewCommand
import com.mad.jellomarkserver.review.port.driving.UpdateReviewUseCase
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

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
        return ReviewResponse.from(review)
    }

    @GetMapping("/api/beautishops/{shopId}/reviews")
    fun listReviews(
        @PathVariable shopId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): PagedReviewsResponse {
        val command = ListReviewsCommand(shopId = shopId, page = page, size = size)
        val result = listReviewsUseCase.execute(command)
        return PagedReviewsResponse.from(result)
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
        return ReviewResponse.from(review)
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
