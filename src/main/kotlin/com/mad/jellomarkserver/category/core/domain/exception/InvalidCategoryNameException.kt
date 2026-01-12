package com.mad.jellomarkserver.category.core.domain.exception

class InvalidCategoryNameException(val categoryName: String) : RuntimeException(
    "Invalid category name: $categoryName"
)
