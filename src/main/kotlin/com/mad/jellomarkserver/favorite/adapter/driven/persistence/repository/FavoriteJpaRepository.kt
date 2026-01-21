package com.mad.jellomarkserver.favorite.adapter.driven.persistence.repository

import com.mad.jellomarkserver.favorite.adapter.driven.persistence.entity.FavoriteJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FavoriteJpaRepository : JpaRepository<FavoriteJpaEntity, UUID> {
    fun findByMemberIdAndShopId(memberId: UUID, shopId: UUID): FavoriteJpaEntity?
    fun findByMemberId(memberId: UUID, pageable: Pageable): Page<FavoriteJpaEntity>
    fun existsByMemberIdAndShopId(memberId: UUID, shopId: UUID): Boolean
    fun deleteByMemberIdAndShopId(memberId: UUID, shopId: UUID)
    fun countByShopId(shopId: UUID): Int
}
