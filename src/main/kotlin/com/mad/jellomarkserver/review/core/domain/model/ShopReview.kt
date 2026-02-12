package com.mad.jellomarkserver.review.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import java.time.Clock
import java.time.Instant

class ShopReview private constructor(
    val id: ReviewId,
    val shopId: ShopId,
    val memberId: MemberId,
    val rating: ReviewRating?,
    val content: ReviewContent?,
    val images: ReviewImages?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val ownerReplyContent: ReplyContent? = null,
    val ownerReplyCreatedAt: Instant? = null
) {
    companion object {
        fun create(
            shopId: ShopId,
            memberId: MemberId,
            rating: ReviewRating?,
            content: ReviewContent?,
            images: ReviewImages?,
            clock: Clock = Clock.systemUTC()
        ): ShopReview {
            val now = Instant.now(clock)
            return ShopReview(
                id = ReviewId.new(),
                shopId = shopId,
                memberId = memberId,
                rating = rating,
                content = content,
                images = images,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstruct(
            id: ReviewId,
            shopId: ShopId,
            memberId: MemberId,
            rating: ReviewRating?,
            content: ReviewContent?,
            images: ReviewImages?,
            createdAt: Instant,
            updatedAt: Instant,
            ownerReplyContent: ReplyContent? = null,
            ownerReplyCreatedAt: Instant? = null
        ): ShopReview {
            return ShopReview(
                id = id,
                shopId = shopId,
                memberId = memberId,
                rating = rating,
                content = content,
                images = images,
                createdAt = createdAt,
                updatedAt = updatedAt,
                ownerReplyContent = ownerReplyContent,
                ownerReplyCreatedAt = ownerReplyCreatedAt
            )
        }
    }

    fun update(
        rating: ReviewRating?,
        content: ReviewContent?,
        images: ReviewImages?,
        clock: Clock = Clock.systemUTC()
    ): ShopReview {
        return ShopReview(
            id = this.id,
            shopId = this.shopId,
            memberId = this.memberId,
            rating = rating,
            content = content,
            images = images,
            createdAt = this.createdAt,
            updatedAt = Instant.now(clock),
            ownerReplyContent = this.ownerReplyContent,
            ownerReplyCreatedAt = this.ownerReplyCreatedAt
        )
    }

    fun reply(content: ReplyContent, clock: Clock = Clock.systemUTC()): ShopReview {
        return ShopReview(
            id = this.id,
            shopId = this.shopId,
            memberId = this.memberId,
            rating = this.rating,
            content = this.content,
            images = this.images,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            ownerReplyContent = content,
            ownerReplyCreatedAt = Instant.now(clock)
        )
    }

    fun deleteReply(): ShopReview {
        return ShopReview(
            id = this.id,
            shopId = this.shopId,
            memberId = this.memberId,
            rating = this.rating,
            content = this.content,
            images = this.images,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            ownerReplyContent = null,
            ownerReplyCreatedAt = null
        )
    }

    fun hasReply(): Boolean = ownerReplyContent != null

    fun isOwnedBy(memberId: MemberId): Boolean {
        return this.memberId == memberId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShopReview) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
