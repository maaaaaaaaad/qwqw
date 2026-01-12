package com.mad.jellomarkserver.category.adapter.driven.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
class ShopCategoryMappingId(
    @Column(name = "shop_id", nullable = false)
    var shopId: UUID = UUID.randomUUID(),

    @Column(name = "category_id", nullable = false)
    var categoryId: UUID = UUID.randomUUID()
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShopCategoryMappingId) return false
        return shopId == other.shopId && categoryId == other.categoryId
    }

    override fun hashCode(): Int {
        return Objects.hash(shopId, categoryId)
    }
}
