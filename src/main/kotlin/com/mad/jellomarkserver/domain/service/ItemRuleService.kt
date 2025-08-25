package com.mad.jellomarkserver.domain.service

import com.mad.jellomarkserver.domain.model.CatalogItem
import org.springframework.stereotype.Component

@Component
class ItemRuleService {
    fun createNew(name: String): CatalogItem {
        val normalized = name.trim()
        return CatalogItem(id = null, name = normalized)
    }
}