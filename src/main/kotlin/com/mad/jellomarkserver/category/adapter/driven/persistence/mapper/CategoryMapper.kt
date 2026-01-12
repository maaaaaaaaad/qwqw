package com.mad.jellomarkserver.category.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.category.adapter.driven.persistence.entity.CategoryJpaEntity
import com.mad.jellomarkserver.category.core.domain.model.Category

interface CategoryMapper {
    fun toEntity(category: Category): CategoryJpaEntity
    fun toDomain(entity: CategoryJpaEntity): Category
}
