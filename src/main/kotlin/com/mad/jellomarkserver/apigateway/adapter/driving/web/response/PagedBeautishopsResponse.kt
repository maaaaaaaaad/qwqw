package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.beautishop.port.driving.PagedBeautishops

data class PagedBeautishopsResponse(
    val items: List<BeautishopResponse>,
    val hasNext: Boolean,
    val totalElements: Long
) {
    companion object {
        fun from(pagedBeautishops: PagedBeautishops): PagedBeautishopsResponse {
            val itemsWithDistances = pagedBeautishops.items.zip(pagedBeautishops.distances)
            return PagedBeautishopsResponse(
                items = itemsWithDistances.map { (beautishop, distance) ->
                    BeautishopResponse.from(beautishop, distance = distance)
                },
                hasNext = pagedBeautishops.hasNext,
                totalElements = pagedBeautishops.totalElements
            )
        }
    }
}
