package com.mad.jellomarkserver.review.port.driven

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.core.domain.model.ReviewId
import com.mad.jellomarkserver.review.core.domain.model.ShopReview
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

data class ReviewStats(
    val averageRating: Double,
    val reviewCount: Int
)

interface ShopReviewPort {
    fun save(review: ShopReview): ShopReview
    fun findById(id: ReviewId): ShopReview?
    fun findByShopId(shopId: ShopId, pageable: Pageable): Page<ShopReview>
    fun findByMemberId(memberId: MemberId): List<ShopReview>
    fun findByMemberId(memberId: MemberId, pageable: Pageable): Page<ShopReview>
    fun existsByShopIdAndMemberId(shopId: ShopId, memberId: MemberId): Boolean
    fun delete(id: ReviewId)
    fun calculateStats(shopId: ShopId): ReviewStats
}
