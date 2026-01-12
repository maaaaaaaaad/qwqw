package com.mad.jellomarkserver.category.core.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class CategoryIdTest {

    @Test
    fun `should create new CategoryId with random UUID`() {
        val id1 = CategoryId.new()
        val id2 = CategoryId.new()

        assertNotNull(id1.value)
        assertNotNull(id2.value)
        assertNotEquals(id1.value, id2.value)
    }

    @Test
    fun `should create CategoryId from existing UUID`() {
        val uuid = UUID.randomUUID()
        val categoryId = CategoryId.from(uuid)

        assertEquals(uuid, categoryId.value)
    }
}
