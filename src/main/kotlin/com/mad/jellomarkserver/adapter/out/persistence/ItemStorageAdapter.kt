package com.mad.jellomarkserver.adapter.out.persistence

import com.mad.jellomarkserver.adapter.out.persistence.entity.ItemRecord
import com.mad.jellomarkserver.adapter.out.persistence.mapper.ItemDataMapper
import com.mad.jellomarkserver.domain.model.CatalogItem
import com.mad.jellomarkserver.domain.port.`out`.StoreItemPort
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class ItemStorageAdapter(
    private val mapper: ItemDataMapper
) : StoreItemPort {
    private val store = ConcurrentHashMap<String, ItemRecord>()

    override fun save(item: CatalogItem): CatalogItem {
        val id = item.id ?: UUID.randomUUID().toString()
        val record = mapper.toRecord(item, id)
        store[id] = record
        return mapper.toDomain(record)
    }
}