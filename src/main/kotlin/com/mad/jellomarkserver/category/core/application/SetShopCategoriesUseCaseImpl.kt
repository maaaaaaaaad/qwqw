package com.mad.jellomarkserver.category.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.category.core.domain.exception.CategoryNotFoundException
import com.mad.jellomarkserver.category.core.domain.exception.UnauthorizedShopAccessException
import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.core.domain.model.CategoryId
import com.mad.jellomarkserver.category.port.driven.CategoryPort
import com.mad.jellomarkserver.category.port.driven.ShopCategoryPort
import com.mad.jellomarkserver.category.port.driving.SetShopCategoriesCommand
import com.mad.jellomarkserver.category.port.driving.SetShopCategoriesUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SetShopCategoriesUseCaseImpl(
    private val beautishopPort: BeautishopPort,
    private val ownerPort: OwnerPort,
    private val categoryPort: CategoryPort,
    private val shopCategoryPort: ShopCategoryPort
) : SetShopCategoriesUseCase {

    @Transactional
    override fun execute(command: SetShopCategoriesCommand): List<Category> {
        val shopId = ShopId.from(UUID.fromString(command.shopId))
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))

        val shops = beautishopPort.findByOwnerId(ownerId)
        val shop = shops.find { it.id == shopId }
            ?: throw if (beautishopPort.findById(shopId) != null) {
                UnauthorizedShopAccessException(command.shopId)
            } else {
                BeautishopNotFoundException(command.shopId)
            }

        val categoryIds = command.categoryIds.map { CategoryId.from(UUID.fromString(it)) }

        if (categoryIds.isNotEmpty()) {
            val existingCategories = categoryPort.findByIds(categoryIds)
            if (existingCategories.size != categoryIds.size) {
                val existingIds = existingCategories.map { it.id.value.toString() }.toSet()
                val missingId = command.categoryIds.first { it !in existingIds }
                throw CategoryNotFoundException(missingId)
            }
        }

        shopCategoryPort.setShopCategories(shopId, categoryIds)

        return shopCategoryPort.findCategoriesByShopId(shopId)
    }
}
