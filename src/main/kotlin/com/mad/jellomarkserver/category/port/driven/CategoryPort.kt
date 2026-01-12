package com.mad.jellomarkserver.category.port.driven

import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.core.domain.model.CategoryId

interface CategoryPort {
    fun save(category: Category): Category
    fun findById(id: CategoryId): Category?
    fun findAll(): List<Category>
    fun findByIds(ids: List<CategoryId>): List<Category>
}
