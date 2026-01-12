package com.mad.jellomarkserver.category.core.domain.model

import com.mad.jellomarkserver.category.core.domain.exception.InvalidCategoryNameException

@JvmInline
value class CategoryName private constructor(val value: String) {
    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 20

        fun of(input: String): CategoryName {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(trimmed.length >= MIN_LENGTH)
                require(trimmed.length <= MAX_LENGTH)
                return CategoryName(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidCategoryNameException(input)
            }
        }
    }
}
