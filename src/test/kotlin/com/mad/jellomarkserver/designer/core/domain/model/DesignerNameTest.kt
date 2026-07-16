package com.mad.jellomarkserver.designer.core.domain.model

import com.mad.jellomarkserver.designer.core.domain.exception.InvalidDesignerNameException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DesignerNameTest {

    @Test
    fun `should create name with valid input`() {
        val name = DesignerName.of("김디자이너")
        assertEquals("김디자이너", name.value)
    }

    @Test
    fun `should trim whitespace`() {
        val name = DesignerName.of("  김디자이너  ")
        assertEquals("김디자이너", name.value)
    }

    @Test
    fun `should throw when name is blank`() {
        assertFailsWith<InvalidDesignerNameException> {
            DesignerName.of("   ")
        }
    }

    @Test
    fun `should throw when name too short`() {
        assertFailsWith<InvalidDesignerNameException> {
            DesignerName.of("김")
        }
    }

    @Test
    fun `should accept minimum length name`() {
        val name = DesignerName.of("김민")
        assertEquals("김민", name.value)
    }

    @Test
    fun `should accept maximum length name`() {
        val input = "a".repeat(30)
        val name = DesignerName.of(input)
        assertEquals(input, name.value)
    }

    @Test
    fun `should throw when name exceeds maximum length`() {
        assertFailsWith<InvalidDesignerNameException> {
            DesignerName.of("a".repeat(31))
        }
    }
}
