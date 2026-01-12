package com.mad.jellomarkserver.category.port.driving

import com.mad.jellomarkserver.category.core.domain.model.Category

interface GetCategoriesUseCase {
    fun execute(): List<Category>
}
