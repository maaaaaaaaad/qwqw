package com.mad.jellomarkserver.review.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.adapter.driven.persistence.mapper.ShopReviewMapper
import com.mad.jellomarkserver.review.core.domain.exception.DuplicateReviewException
import com.mad.jellomarkserver.review.core.domain.model.ReviewId
import com.mad.jellomarkserver.review.core.domain.model.ShopReview
import com.mad.jellomarkserver.review.port.driven.ReviewStats
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ShopReviewPersistenceAdapter(
    private val jpaRepository: ShopReviewJpaRepository,
    private val mapper: ShopReviewMapper,
    private val constraintTranslator: ConstraintViolationTranslator
) : ShopReviewPort {

    override fun save(review: ShopReview): ShopReview {
        try {
            val entity = mapper.toEntity(review)
            val saved = jpaRepository.saveAndFlush(entity)
            return mapper.toDomain(saved)
        } catch (e: DataIntegrityViolationException) {
            constraintTranslator.translateAndThrow(
                e, mapOf(
                    "uk_shop_reviews_shop_member" to {
                        DuplicateReviewException(
                            review.shopId.value.toString(),
                            review.memberId.value.toString()
                        )
                    }
                )
            )
        }
    }

    override fun findById(id: ReviewId): ShopReview? {
        return jpaRepository.findById(id.value).map { mapper.toDomain(it) }.orElse(null)
    }

    override fun findByShopId(shopId: ShopId, pageable: Pageable): Page<ShopReview> {
        return jpaRepository.findByShopId(shopId.value, pageable).map { mapper.toDomain(it) }
    }

    override fun findByMemberId(memberId: MemberId): List<ShopReview> {
        return jpaRepository.findByMemberId(memberId.value).map { mapper.toDomain(it) }
    }

    override fun findByMemberId(memberId: MemberId, pageable: Pageable): Page<ShopReview> {
        return jpaRepository.findByMemberId(memberId.value, pageable).map { mapper.toDomain(it) }
    }

    override fun existsByShopIdAndMemberId(shopId: ShopId, memberId: MemberId): Boolean {
        return jpaRepository.existsByShopIdAndMemberId(shopId.value, memberId.value)
    }

    override fun delete(id: ReviewId) {
        jpaRepository.deleteById(id.value)
    }

    override fun calculateStats(shopId: ShopId): ReviewStats {
        val count = jpaRepository.countByShopId(shopId.value)
        val average = jpaRepository.averageRatingByShopId(shopId.value)
        return ReviewStats(averageRating = average, reviewCount = count)
    }
}
