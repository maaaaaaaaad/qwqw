package com.mad.jellomarkserver.category.core.domain.model

import com.mad.jellomarkserver.category.core.domain.exception.InvalidCategoryNameException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class CategoryNameTest {

    @Test
    fun `should create CategoryName with valid input`() {
        val name = CategoryName.of("네일")
        assertEquals("네일", name.value)
    }

    @Test
    fun `should create CategoryName with various valid categories`() {
        val categories = listOf("네일", "속눈썹", "왁싱", "피부관리", "태닝", "발관리")
        categories.forEach { category ->
            val name = CategoryName.of(category)
            assertEquals(category, name.value)
        }
    }

    @Test
    fun `should trim whitespace from input`() {
        val name = CategoryName.of("  네일  ")
        assertEquals("네일", name.value)
    }

    @Test
    fun `should throw InvalidCategoryNameException when name is blank`() {
        assertFailsWith<InvalidCategoryNameException> {
            CategoryName.of("")
        }
    }

    @Test
    fun `should throw InvalidCategoryNameException when name is only whitespace`() {
        assertFailsWith<InvalidCategoryNameException> {
            CategoryName.of("   ")
        }
    }

    @Test
    fun `should throw InvalidCategoryNameException when name exceeds max length`() {
        val longName = "a".repeat(21)
        assertFailsWith<InvalidCategoryNameException> {
            CategoryName.of(longName)
        }
    }

    @Test
    fun `should create CategoryName with max length`() {
        val maxLengthName = "a".repeat(20)
        val name = CategoryName.of(maxLengthName)
        assertEquals(maxLengthName, name.value)
    }
}
