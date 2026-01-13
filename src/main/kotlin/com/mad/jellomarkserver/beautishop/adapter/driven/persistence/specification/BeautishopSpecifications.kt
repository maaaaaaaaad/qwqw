package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.specification

import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.entity.BeautishopJpaEntity
import com.mad.jellomarkserver.category.adapter.driven.persistence.entity.ShopCategoryMappingJpaEntity
import org.springframework.data.jpa.domain.Specification
import java.util.*

object BeautishopSpecifications {

    fun hasCategory(categoryId: UUID?): Specification<BeautishopJpaEntity> {
        return Specification { root, query, cb ->
            if (categoryId == null || query == null) {
                null
            } else {
                val subquery = query.subquery(UUID::class.java)
                val mapping = subquery.from(ShopCategoryMappingJpaEntity::class.java)
                subquery.select(mapping.get<UUID>("id").get("shopId"))
                    .where(cb.equal(mapping.get<Any>("id").get<UUID>("categoryId"), categoryId))
                cb.`in`(root.get<UUID>("id")).value(subquery)
            }
        }
    }

    fun hasMinRating(minRating: Double?): Specification<BeautishopJpaEntity> {
        return Specification { root, _, cb ->
            if (minRating == null) {
                null
            } else {
                cb.greaterThanOrEqualTo(root.get("averageRating"), minRating)
            }
        }
    }
}
