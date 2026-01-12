package com.mad.jellomarkserver.category.core.application

import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.core.domain.model.CategoryId
import com.mad.jellomarkserver.category.core.domain.model.CategoryName
import com.mad.jellomarkserver.category.port.driven.CategoryPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class GetCategoriesUseCaseImplTest {

    @Mock
    private lateinit var categoryPort: CategoryPort

    private lateinit var useCase: GetCategoriesUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = GetCategoriesUseCaseImpl(categoryPort)
    }

    @Test
    fun `should return all categories`() {
        val categories = listOf(
            createCategory("네일"),
            createCategory("속눈썹"),
            createCategory("왁싱")
        )
        `when`(categoryPort.findAll()).thenReturn(categories)

        val result = useCase.execute()

        assertEquals(3, result.size)
        assertEquals("네일", result[0].name.value)
        assertEquals("속눈썹", result[1].name.value)
        assertEquals("왁싱", result[2].name.value)
    }

    @Test
    fun `should return empty list when no categories exist`() {
        `when`(categoryPort.findAll()).thenReturn(emptyList())

        val result = useCase.execute()

        assertTrue(result.isEmpty())
    }

    private fun createCategory(name: String): Category {
        return Category.reconstruct(
            id = CategoryId.new(),
            name = CategoryName.of(name),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
