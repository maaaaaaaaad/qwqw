package com.mad.jellomarkserver.application.service

import com.mad.jellomarkserver.domain.model.CatalogItem
import com.mad.jellomarkserver.domain.port.`in`.RegisterItemUseCase
import com.mad.jellomarkserver.domain.port.`out`.StoreItemPort
import com.mad.jellomarkserver.domain.service.ItemRuleService
import org.springframework.stereotype.Service

@Service
class RegisterItemHandler(
    private val rules: ItemRuleService,
    private val store: StoreItemPort
) : RegisterItemUseCase {
    override fun register(name: String): CatalogItem {
        val created = rules.createNew(name)
        return store.save(created)
    }
}