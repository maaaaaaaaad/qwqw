package com.mad.jellomarkserver.designer.core.domain.model

import com.mad.jellomarkserver.designer.core.domain.exception.InvalidDesignerNicknameException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DesignerNicknameTest {

    @Test
    fun `should create nickname with valid input`() {
        val nickname = DesignerNickname.of("네일요정")
        assertEquals("네일요정", nickname.value)
    }

    @Test
    fun `should trim whitespace`() {
        val nickname = DesignerNickname.of("  네일요정  ")
        assertEquals("네일요정", nickname.value)
    }

    @Test
    fun `should throw when nickname exceeds maximum length`() {
        assertFailsWith<InvalidDesignerNicknameException> {
            DesignerNickname.of("a".repeat(31))
        }
    }

    @Test
    fun `should accept maximum length nickname`() {
        val input = "a".repeat(30)
        val nickname = DesignerNickname.of(input)
        assertEquals(input, nickname.value)
    }

    @Test
    fun `ofNullable should return null when input is null`() {
        assertNull(DesignerNickname.ofNullable(null))
    }

    @Test
    fun `ofNullable should return null when input is blank`() {
        assertNull(DesignerNickname.ofNullable("   "))
    }

    @Test
    fun `ofNullable should return nickname when input is valid`() {
        val nickname = DesignerNickname.ofNullable("네일요정")
        assertEquals("네일요정", nickname?.value)
    }
}
