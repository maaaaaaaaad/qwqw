package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.CategoryResponse
import com.mad.jellomarkserver.category.port.driving.GetCategoriesUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val getCategoriesUseCase: GetCategoriesUseCase
) {

    @GetMapping
    fun getCategories(): ResponseEntity<List<CategoryResponse>> {
        val categories = getCategoriesUseCase.execute()
        val response = categories.map { CategoryResponse.from(it) }
        return ResponseEntity.ok(response)
    }
}
