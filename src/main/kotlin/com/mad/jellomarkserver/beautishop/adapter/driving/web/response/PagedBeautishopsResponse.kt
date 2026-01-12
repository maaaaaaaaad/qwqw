package com.mad.jellomarkserver.beautishop.adapter.driving.web.response

import com.mad.jellomarkserver.beautishop.port.driving.PagedBeautishops

data class PagedBeautishopsResponse(
    val items: List<BeautishopResponse>,
    val hasNext: Boolean,
    val totalElements: Long
) {
    companion object {
        fun from(pagedBeautishops: PagedBeautishops): PagedBeautishopsResponse {
            return PagedBeautishopsResponse(
                items = pagedBeautishops.items.map { BeautishopResponse.from(it) },
                hasNext = pagedBeautishops.hasNext,
                totalElements = pagedBeautishops.totalElements
            )
        }
    }
}
