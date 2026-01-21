package com.mad.jellomarkserver.review.adapter.driven.persistence.repository

import com.mad.jellomarkserver.review.adapter.driven.persistence.entity.ShopReviewJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ShopReviewJpaRepository : JpaRepository<ShopReviewJpaEntity, UUID> {
    fun findByShopId(shopId: UUID, pageable: Pageable): Page<ShopReviewJpaEntity>
    fun findByMemberId(memberId: UUID): List<ShopReviewJpaEntity>
    fun findByMemberId(memberId: UUID, pageable: Pageable): Page<ShopReviewJpaEntity>
    fun existsByShopIdAndMemberId(shopId: UUID, memberId: UUID): Boolean
    fun countByShopId(shopId: UUID): Int

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM ShopReviewJpaEntity r WHERE r.shopId = :shopId AND r.rating IS NOT NULL")
    fun averageRatingByShopId(shopId: UUID): Double
}
