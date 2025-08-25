package com.mad.jellomarkserver.domain.port.`out`

import com.mad.jellomarkserver.domain.model.CatalogItem

interface StoreItemPort {
    fun save(item: CatalogItem): CatalogItem
}