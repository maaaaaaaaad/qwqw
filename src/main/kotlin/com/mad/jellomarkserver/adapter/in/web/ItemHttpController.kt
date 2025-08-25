package com.mad.jellomarkserver.adapter.`in`.web

import com.mad.jellomarkserver.domain.model.CatalogItem
import com.mad.jellomarkserver.domain.port.`in`.RegisterItemUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/items")
class ItemHttpController(
    private val register: RegisterItemUseCase
) {
    @PostMapping
    fun create(@RequestParam name: String): CatalogItem = register.register(name)
}