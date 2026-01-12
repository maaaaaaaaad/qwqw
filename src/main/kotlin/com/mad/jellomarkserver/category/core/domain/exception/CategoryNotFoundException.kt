package com.mad.jellomarkserver.category.core.domain.exception

class CategoryNotFoundException(val categoryId: String) : RuntimeException(
    "Category not found: $categoryId"
)
