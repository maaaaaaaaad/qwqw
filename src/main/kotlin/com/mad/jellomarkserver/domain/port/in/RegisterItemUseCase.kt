package com.mad.jellomarkserver.domain.port.`in`

import com.mad.jellomarkserver.domain.model.CatalogItem

interface RegisterItemUseCase {
    fun register(name: String): CatalogItem
}