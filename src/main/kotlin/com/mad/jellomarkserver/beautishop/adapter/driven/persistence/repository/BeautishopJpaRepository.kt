package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.entity.BeautishopJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface BeautishopJpaRepository : JpaRepository<BeautishopJpaEntity, UUID>,
    JpaSpecificationExecutor<BeautishopJpaEntity> {
    fun findByOwnerId(ownerId: UUID): List<BeautishopJpaEntity>
    fun findByShopRegNum(shopRegNum: String): BeautishopJpaEntity?

    @Query("SELECT b.ownerId FROM BeautishopJpaEntity b WHERE b.id = :shopId")
    fun findOwnerIdByShopId(shopId: UUID): UUID?

    @Modifying
    @Query("UPDATE BeautishopJpaEntity b SET b.averageRating = :averageRating, b.reviewCount = :reviewCount WHERE b.id = :id")
    fun updateStats(id: UUID, averageRating: Double, reviewCount: Int)
}
