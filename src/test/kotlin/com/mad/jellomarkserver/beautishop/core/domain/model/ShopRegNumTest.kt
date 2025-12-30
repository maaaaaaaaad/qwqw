package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopRegNumException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ShopRegNumTest {

    @Test
    fun `should create ShopRegNum with valid 10-digit number`() {
        val regNum = ShopRegNum.of("1234567890")
        assertEquals("123-45-67890", regNum.value)
    }

    @Test
    fun `should create ShopRegNum with valid formatted number`() {
        val regNum = ShopRegNum.of("123-45-67890")
        assertEquals("123-45-67890", regNum.value)
    }

    @Test
    fun `should trim whitespace from registration number`() {
        val regNum = ShopRegNum.of("  123-45-67890  ")
        assertEquals("123-45-67890", regNum.value)
    }

    @Test
    fun `should normalize format to XXX-XX-XXXXX`() {
        val regNum = ShopRegNum.of("12345-67890")
        assertEquals("123-45-67890", regNum.value)
    }

    @Test
    fun `should throw InvalidShopRegNumException when number is blank`() {
        assertFailsWith<InvalidShopRegNumException> {
            ShopRegNum.of("   ")
        }
    }

    @Test
    fun `should throw InvalidShopRegNumException when number is empty`() {
        assertFailsWith<InvalidShopRegNumException> {
            ShopRegNum.of("")
        }
    }

    @Test
    fun `should throw InvalidShopRegNumException when number is too short`() {
        assertFailsWith<InvalidShopRegNumException> {
            ShopRegNum.of("123456789")
        }
    }

    @Test
    fun `should throw InvalidShopRegNumException when number is too long`() {
        assertFailsWith<InvalidShopRegNumException> {
            ShopRegNum.of("12345678901")
        }
    }

    @Test
    fun `should throw InvalidShopRegNumException when number contains letters`() {
        assertFailsWith<InvalidShopRegNumException> {
            ShopRegNum.of("123-45-6789A")
        }
    }

    @Test
    fun `should throw InvalidShopRegNumException when number contains special characters except hyphen`() {
        assertFailsWith<InvalidShopRegNumException> {
            ShopRegNum.of("123-45-6789!")
        }
    }
}
