package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.favorite.core.domain.model.Favorite
import java.time.Instant
import java.util.*

data class FavoriteResponse(
    val id: UUID,
    val shopId: UUID,
    val createdAt: Instant,
    val shop: ShopSummary?
) {
    data class ShopSummary(
        val id: UUID,
        val name: String,
        val address: String,
        val image: String?,
        val averageRating: Double,
        val reviewCount: Int
    ) {
        companion object {
            fun from(beautishop: Beautishop): ShopSummary {
                return ShopSummary(
                    id = beautishop.id.value,
                    name = beautishop.name.value,
                    address = beautishop.address.value,
                    image = beautishop.image?.value,
                    averageRating = beautishop.averageRating.value,
                    reviewCount = beautishop.reviewCount.value
                )
            }
        }
    }

    companion object {
        fun from(favorite: Favorite, beautishop: Beautishop? = null): FavoriteResponse {
            return FavoriteResponse(
                id = favorite.id.value,
                shopId = favorite.shopId.value,
                createdAt = favorite.createdAt,
                shop = beautishop?.let { ShopSummary.from(it) }
            )
        }
    }
}
