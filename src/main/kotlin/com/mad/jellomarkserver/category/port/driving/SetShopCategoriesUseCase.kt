package com.mad.jellomarkserver.category.port.driving

import com.mad.jellomarkserver.category.core.domain.model.Category

interface SetShopCategoriesUseCase {
    fun execute(command: SetShopCategoriesCommand): List<Category>
}
