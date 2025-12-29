package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopDescriptionException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ShopDescriptionTest {

    @Test
    fun `should create ShopDescription with valid description`() {
        val description = ShopDescription.of("아름다운 네일샵입니다")
        assertEquals("아름다운 네일샵입니다", description.value)
    }

    @Test
    fun `should create ShopDescription with null when input is null`() {
        val description = ShopDescription.ofNullable(null)
        assertNull(description)
    }

    @Test
    fun `should create ShopDescription with null when input is blank`() {
        val description = ShopDescription.ofNullable("   ")
        assertNull(description)
    }

    @Test
    fun `should create ShopDescription with null when input is empty`() {
        val description = ShopDescription.ofNullable("")
        assertNull(description)
    }

    @Test
    fun `should trim whitespace from description`() {
        val description = ShopDescription.of("  아름다운 네일샵입니다  ")
        assertEquals("아름다운 네일샵입니다", description.value)
    }

    @Test
    fun `should allow description with maximum length`() {
        val longDescription = "A".repeat(500)
        val description = ShopDescription.of(longDescription)
        assertEquals(longDescription, description.value)
    }

    @Test
    fun `should allow description with multiline text`() {
        val multiline = "아름다운 네일샵입니다.\n영업시간: 09:00-18:00"
        val description = ShopDescription.of(multiline)
        assertEquals(multiline, description.value)
    }

    @Test
    fun `should throw InvalidShopDescriptionException when description is too long`() {
        val tooLong = "A".repeat(501)
        assertFailsWith<InvalidShopDescriptionException> {
            ShopDescription.of(tooLong)
        }
    }

    @Test
    fun `should throw InvalidShopDescriptionException when description is blank`() {
        assertFailsWith<InvalidShopDescriptionException> {
            ShopDescription.of("   ")
        }
    }
}
