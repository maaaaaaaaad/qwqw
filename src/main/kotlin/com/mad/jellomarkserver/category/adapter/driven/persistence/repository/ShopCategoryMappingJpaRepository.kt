package com.mad.jellomarkserver.category.adapter.driven.persistence.repository

import com.mad.jellomarkserver.category.adapter.driven.persistence.entity.ShopCategoryMappingId
import com.mad.jellomarkserver.category.adapter.driven.persistence.entity.ShopCategoryMappingJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ShopCategoryMappingJpaRepository : JpaRepository<ShopCategoryMappingJpaEntity, ShopCategoryMappingId> {

    @Query("SELECT m.id.categoryId FROM ShopCategoryMappingJpaEntity m WHERE m.id.shopId = :shopId")
    fun findCategoryIdsByShopId(shopId: UUID): List<UUID>

    @Query("SELECT m.id.shopId FROM ShopCategoryMappingJpaEntity m WHERE m.id.categoryId = :categoryId")
    fun findShopIdsByCategoryId(categoryId: UUID): List<UUID>

    @Modifying
    @Query("DELETE FROM ShopCategoryMappingJpaEntity m WHERE m.id.shopId = :shopId")
    fun deleteAllByShopId(shopId: UUID)

    fun existsByIdShopIdAndIdCategoryId(shopId: UUID, categoryId: UUID): Boolean
}
