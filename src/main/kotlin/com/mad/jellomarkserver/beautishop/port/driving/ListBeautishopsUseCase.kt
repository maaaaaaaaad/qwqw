package com.mad.jellomarkserver.beautishop.port.driving

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop

fun interface ListBeautishopsUseCase {
    fun execute(command: ListBeautishopsCommand): PagedBeautishops
}

data class PagedBeautishops(
    val items: List<Beautishop>,
    val hasNext: Boolean,
    val totalElements: Long
)
