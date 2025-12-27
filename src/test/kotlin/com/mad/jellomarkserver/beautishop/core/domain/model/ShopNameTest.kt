package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopNameException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ShopNameTest {

    @Test
    fun `should create ShopName with valid name`() {
        val name = ShopName.of("Beautiful Salon")
        assertEquals("Beautiful Salon", name.value)
    }

    @Test
    fun `should trim whitespace from shop name`() {
        val name = ShopName.of("  Beautiful Salon  ")
        assertEquals("Beautiful Salon", name.value)
    }

    @Test
    fun `should allow shop name with minimum length`() {
        val name = ShopName.of("AB")
        assertEquals("AB", name.value)
    }

    @Test
    fun `should allow shop name with maximum length`() {
        val longName = "A".repeat(50)
        val name = ShopName.of(longName)
        assertEquals(longName, name.value)
    }

    @Test
    fun `should allow shop name with spaces`() {
        val name = ShopName.of("Beauty & Nail Salon")
        assertEquals("Beauty & Nail Salon", name.value)
    }

    @Test
    fun `should throw InvalidShopNameException when name is blank`() {
        assertFailsWith<InvalidShopNameException> {
            ShopName.of("   ")
        }
    }

    @Test
    fun `should throw InvalidShopNameException when name is empty`() {
        assertFailsWith<InvalidShopNameException> {
            ShopName.of("")
        }
    }

    @Test
    fun `should throw InvalidShopNameException when name is too short`() {
        assertFailsWith<InvalidShopNameException> {
            ShopName.of("A")
        }
    }

    @Test
    fun `should throw InvalidShopNameException when name is too long`() {
        val tooLong = "A".repeat(51)
        assertFailsWith<InvalidShopNameException> {
            ShopName.of(tooLong)
        }
    }

    @Test
    fun `should allow Korean characters`() {
        val name = ShopName.of("아름다운 살롱")
        assertEquals("아름다운 살롱", name.value)
    }

    @Test
    fun `should allow special characters`() {
        val name = ShopName.of("Salon #1 & Co.")
        assertEquals("Salon #1 & Co.", name.value)
    }
}
