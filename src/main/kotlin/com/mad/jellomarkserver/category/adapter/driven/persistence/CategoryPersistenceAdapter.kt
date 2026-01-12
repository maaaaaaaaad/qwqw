package com.mad.jellomarkserver.category.adapter.driven.persistence

import com.mad.jellomarkserver.category.adapter.driven.persistence.mapper.CategoryMapper
import com.mad.jellomarkserver.category.adapter.driven.persistence.repository.CategoryJpaRepository
import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.core.domain.model.CategoryId
import com.mad.jellomarkserver.category.port.driven.CategoryPort
import org.springframework.stereotype.Repository

@Repository
class CategoryPersistenceAdapter(
    private val categoryJpaRepository: CategoryJpaRepository,
    private val categoryMapper: CategoryMapper
) : CategoryPort {

    override fun save(category: Category): Category {
        val entity = categoryMapper.toEntity(category)
        val saved = categoryJpaRepository.save(entity)
        return categoryMapper.toDomain(saved)
    }

    override fun findById(id: CategoryId): Category? {
        return categoryJpaRepository.findById(id.value)
            .map { categoryMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findAll(): List<Category> {
        return categoryJpaRepository.findAll()
            .map { categoryMapper.toDomain(it) }
    }

    override fun findByIds(ids: List<CategoryId>): List<Category> {
        val uuids = ids.map { it.value }
        return categoryJpaRepository.findAllById(uuids)
            .map { categoryMapper.toDomain(it) }
    }
}
