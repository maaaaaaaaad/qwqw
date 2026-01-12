package com.mad.jellomarkserver.category.port.driven

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.core.domain.model.CategoryId

interface ShopCategoryPort {
    fun findCategoriesByShopId(shopId: ShopId): List<Category>
    fun setShopCategories(shopId: ShopId, categoryIds: List<CategoryId>)
    fun addCategory(shopId: ShopId, categoryId: CategoryId)
    fun removeCategory(shopId: ShopId, categoryId: CategoryId)
    fun removeAllCategories(shopId: ShopId)
}
