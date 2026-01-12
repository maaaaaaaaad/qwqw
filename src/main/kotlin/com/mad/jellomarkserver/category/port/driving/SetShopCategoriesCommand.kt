package com.mad.jellomarkserver.category.port.driving

data class SetShopCategoriesCommand(
    val shopId: String,
    val ownerId: String,
    val categoryIds: List<String>
)
