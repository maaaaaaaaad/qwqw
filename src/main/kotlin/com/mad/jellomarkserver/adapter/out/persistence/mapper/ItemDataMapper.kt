package com.mad.jellomarkserver.adapter.out.persistence.mapper

import com.mad.jellomarkserver.adapter.out.persistence.entity.ItemRecord
import com.mad.jellomarkserver.domain.model.CatalogItem
import org.springframework.stereotype.Component

@Component
class ItemDataMapper {
    fun toRecord(item: CatalogItem, id: String): ItemRecord = ItemRecord(id = id, name = item.name)
    fun toDomain(record: ItemRecord): CatalogItem = CatalogItem(id = record.id, name = record.name)
}