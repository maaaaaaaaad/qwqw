package com.mad.jellomarkserver.category.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.category.adapter.driven.persistence.entity.CategoryJpaEntity
import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.core.domain.model.CategoryId
import com.mad.jellomarkserver.category.core.domain.model.CategoryName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class CategoryMapperImplTest {

    private lateinit var mapper: CategoryMapper

    @BeforeEach
    fun setup() {
        mapper = CategoryMapperImpl()
    }

    @Test
    fun `should map Category to CategoryJpaEntity`() {
        val id = CategoryId.new()
        val name = CategoryName.of("네일")
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-01-10T00:00:00Z")

        val category = Category.reconstruct(
            id = id,
            name = name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entity = mapper.toEntity(category)

        assertEquals(id.value, entity.id)
        assertEquals("네일", entity.name)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should map CategoryJpaEntity to Category`() {
        val id = UUID.randomUUID()
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-01-10T00:00:00Z")

        val entity = CategoryJpaEntity(
            id = id,
            name = "속눈썹",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val category = mapper.toDomain(entity)

        assertEquals(id, category.id.value)
        assertEquals("속눈썹", category.name.value)
        assertEquals(createdAt, category.createdAt)
        assertEquals(updatedAt, category.updatedAt)
    }

    @Test
    fun `should map all predefined categories correctly`() {
        val categories = listOf("네일", "속눈썹", "왁싱", "피부관리", "태닝", "발관리")

        categories.forEach { categoryName ->
            val entity = CategoryJpaEntity(
                id = UUID.randomUUID(),
                name = categoryName,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            val category = mapper.toDomain(entity)
            assertEquals(categoryName, category.name.value)

            val mappedEntity = mapper.toEntity(category)
            assertEquals(categoryName, mappedEntity.name)
        }
    }
}
