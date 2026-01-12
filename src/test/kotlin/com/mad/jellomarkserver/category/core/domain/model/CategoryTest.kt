package com.mad.jellomarkserver.category.core.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class CategoryTest {

    private val fixedClock = Clock.fixed(
        Instant.parse("2024-01-15T10:00:00Z"),
        ZoneId.of("UTC")
    )

    @Test
    fun `should create Category with valid data`() {
        val name = CategoryName.of("네일")
        val category = Category.create(name = name, clock = fixedClock)

        assertNotNull(category.id)
        assertEquals("네일", category.name.value)
        assertEquals(fixedClock.instant(), category.createdAt)
        assertEquals(fixedClock.instant(), category.updatedAt)
    }

    @Test
    fun `should reconstruct Category from stored data`() {
        val id = CategoryId.new()
        val name = CategoryName.of("속눈썹")
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-01-10T00:00:00Z")

        val category = Category.reconstruct(
            id = id,
            name = name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, category.id)
        assertEquals("속눈썹", category.name.value)
        assertEquals(createdAt, category.createdAt)
        assertEquals(updatedAt, category.updatedAt)
    }

    @Test
    fun `should create different Category instances with unique IDs`() {
        val name = CategoryName.of("왁싱")
        val category1 = Category.create(name = name)
        val category2 = Category.create(name = name)

        assertNotEquals(category1.id.value, category2.id.value)
    }
}
