package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.favorite.core.domain.model.Favorite
import org.springframework.data.domain.Page

data class PagedFavoritesResponse(
    val items: List<FavoriteResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
) {
    companion object {
        fun from(
            page: Page<Favorite>,
            shopMap: Map<String, Beautishop>
        ): PagedFavoritesResponse {
            return PagedFavoritesResponse(
                items = page.content.map { favorite ->
                    val shop = shopMap[favorite.shopId.value.toString()]
                    FavoriteResponse.from(favorite, shop)
                },
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                page = page.number,
                size = page.size,
                hasNext = page.hasNext()
            )
        }
    }
}
