package com.mad.jellomarkserver.category.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.category.adapter.driven.persistence.entity.CategoryJpaEntity
import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.core.domain.model.CategoryId
import com.mad.jellomarkserver.category.core.domain.model.CategoryName
import org.springframework.stereotype.Component

@Component
class CategoryMapperImpl : CategoryMapper {

    override fun toEntity(category: Category): CategoryJpaEntity {
        return CategoryJpaEntity(
            id = category.id.value,
            name = category.name.value,
            createdAt = category.createdAt,
            updatedAt = category.updatedAt
        )
    }

    override fun toDomain(entity: CategoryJpaEntity): Category {
        return Category.reconstruct(
            id = CategoryId.from(entity.id),
            name = CategoryName.of(entity.name),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
