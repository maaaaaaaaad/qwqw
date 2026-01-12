package com.mad.jellomarkserver.category.adapter.driven.persistence

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.category.adapter.driven.persistence.entity.ShopCategoryMappingId
import com.mad.jellomarkserver.category.adapter.driven.persistence.entity.ShopCategoryMappingJpaEntity
import com.mad.jellomarkserver.category.adapter.driven.persistence.mapper.CategoryMapper
import com.mad.jellomarkserver.category.adapter.driven.persistence.repository.CategoryJpaRepository
import com.mad.jellomarkserver.category.adapter.driven.persistence.repository.ShopCategoryMappingJpaRepository
import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.core.domain.model.CategoryId
import com.mad.jellomarkserver.category.port.driven.ShopCategoryPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Repository
class ShopCategoryPersistenceAdapter(
    private val shopCategoryMappingJpaRepository: ShopCategoryMappingJpaRepository,
    private val categoryJpaRepository: CategoryJpaRepository,
    private val categoryMapper: CategoryMapper
) : ShopCategoryPort {

    override fun findCategoriesByShopId(shopId: ShopId): List<Category> {
        val categoryIds = shopCategoryMappingJpaRepository.findCategoryIdsByShopId(shopId.value)
        if (categoryIds.isEmpty()) return emptyList()

        return categoryJpaRepository.findAllById(categoryIds)
            .map { categoryMapper.toDomain(it) }
    }

    @Transactional
    override fun setShopCategories(shopId: ShopId, categoryIds: List<CategoryId>) {
        shopCategoryMappingJpaRepository.deleteAllByShopId(shopId.value)

        val now = Instant.now()
        val mappings = categoryIds.map { categoryId ->
            ShopCategoryMappingJpaEntity(
                id = ShopCategoryMappingId(shopId = shopId.value, categoryId = categoryId.value),
                createdAt = now
            )
        }
        shopCategoryMappingJpaRepository.saveAll(mappings)
    }

    override fun addCategory(shopId: ShopId, categoryId: CategoryId) {
        val exists = shopCategoryMappingJpaRepository.existsByIdShopIdAndIdCategoryId(
            shopId.value,
            categoryId.value
        )
        if (!exists) {
            val mapping = ShopCategoryMappingJpaEntity(
                id = ShopCategoryMappingId(shopId = shopId.value, categoryId = categoryId.value),
                createdAt = Instant.now()
            )
            shopCategoryMappingJpaRepository.save(mapping)
        }
    }

    override fun removeCategory(shopId: ShopId, categoryId: CategoryId) {
        val mappingId = ShopCategoryMappingId(shopId = shopId.value, categoryId = categoryId.value)
        shopCategoryMappingJpaRepository.deleteById(mappingId)
    }

    @Transactional
    override fun removeAllCategories(shopId: ShopId) {
        shopCategoryMappingJpaRepository.deleteAllByShopId(shopId.value)
    }
}
