package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateReviewRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.ReplyToReviewRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.UpdateReviewRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.PagedReviewsResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.ReviewResponse
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
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
    private val getMemberReviewsUseCase: GetMemberReviewsUseCase,
    private val replyToReviewUseCase: ReplyToReviewUseCase,
    private val deleteReviewReplyUseCase: DeleteReviewReplyUseCase,
    private val memberPort: MemberPort,
    private val beautishopPort: BeautishopPort
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

    @GetMapping("/api/reviews/me")
    fun getMyReviews(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt,desc") sort: String,
        servletRequest: HttpServletRequest
    ): PagedReviewsResponse {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can view their reviews")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = GetMemberReviewsCommand(
            memberId = member.id.value.toString(),
            page = page,
            size = size,
            sort = sort
        )

        val result = getMemberReviewsUseCase.execute(command)

        val memberDisplayNames = mapOf(member.id.value.toString() to member.displayName.value)

        val shopIds = result.items.map { ShopId.from(it.shopId.value) }
        val shops = beautishopPort.findByIds(shopIds)
        val shopNames = shops.associate {
            it.id.value.toString() to it.name.value
        }
        val shopImages = shops.associate {
            it.id.value.toString() to (it.images.values.firstOrNull()?.value ?: "")
        }.filterValues { it.isNotEmpty() }

        return PagedReviewsResponse.from(result, memberDisplayNames, shopNames, shopImages)
    }

    @PutMapping("/api/beautishops/{shopId}/reviews/{reviewId}/reply")
    fun replyToReview(
        @PathVariable shopId: String,
        @PathVariable reviewId: String,
        @RequestBody request: ReplyToReviewRequest,
        servletRequest: HttpServletRequest
    ) {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can reply to reviews")
        }

        val command = ReplyToReviewCommand(
            shopId = shopId,
            reviewId = reviewId,
            ownerEmail = email,
            content = request.content
        )

        replyToReviewUseCase.execute(command)
    }

    @DeleteMapping("/api/beautishops/{shopId}/reviews/{reviewId}/reply")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteReviewReply(
        @PathVariable shopId: String,
        @PathVariable reviewId: String,
        servletRequest: HttpServletRequest
    ) {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can delete review replies")
        }

        val command = DeleteReviewReplyCommand(
            shopId = shopId,
            reviewId = reviewId,
            ownerEmail = email
        )

        deleteReviewReplyUseCase.execute(command)
    }
}
