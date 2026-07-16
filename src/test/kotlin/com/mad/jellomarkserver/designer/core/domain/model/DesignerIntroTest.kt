package com.mad.jellomarkserver.designer.core.domain.model

import com.mad.jellomarkserver.designer.core.domain.exception.InvalidDesignerIntroException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DesignerIntroTest {

    @Test
    fun `should create intro with valid input`() {
        val intro = DesignerIntro.of("10년 경력 네일 아티스트입니다.")
        assertEquals("10년 경력 네일 아티스트입니다.", intro.value)
    }

    @Test
    fun `should trim whitespace`() {
        val intro = DesignerIntro.of("  안녕하세요  ")
        assertEquals("안녕하세요", intro.value)
    }

    @Test
    fun `should throw when intro exceeds maximum length`() {
        assertFailsWith<InvalidDesignerIntroException> {
            DesignerIntro.of("a".repeat(501))
        }
    }

    @Test
    fun `should accept maximum length intro`() {
        val input = "a".repeat(500)
        val intro = DesignerIntro.of(input)
        assertEquals(input, intro.value)
    }

    @Test
    fun `ofNullable should return null when input is null`() {
        assertNull(DesignerIntro.ofNullable(null))
    }

    @Test
    fun `ofNullable should return null when input is blank`() {
        assertNull(DesignerIntro.ofNullable("   "))
    }

    @Test
    fun `ofNullable should return intro when input is valid`() {
        val intro = DesignerIntro.ofNullable("소개글")
        assertEquals("소개글", intro?.value)
    }
}
