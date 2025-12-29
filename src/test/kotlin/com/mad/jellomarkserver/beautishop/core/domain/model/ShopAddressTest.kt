package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopAddressException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ShopAddressTest {

    @Test
    fun `should create ShopAddress with valid address`() {
        val address = ShopAddress.of("서울특별시 강남구 테헤란로 123")
        assertEquals("서울특별시 강남구 테헤란로 123", address.value)
    }

    @Test
    fun `should trim whitespace from address`() {
        val address = ShopAddress.of("  서울특별시 강남구 테헤란로 123  ")
        assertEquals("서울특별시 강남구 테헤란로 123", address.value)
    }

    @Test
    fun `should allow address with minimum length`() {
        val address = ShopAddress.of("서울시 강남")
        assertEquals("서울시 강남", address.value)
    }

    @Test
    fun `should allow address with maximum length`() {
        val longAddress = "A".repeat(200)
        val address = ShopAddress.of(longAddress)
        assertEquals(longAddress, address.value)
    }

    @Test
    fun `should allow address with special characters`() {
        val address = ShopAddress.of("서울시 강남구 삼성동 123-45 (테헤란로)")
        assertEquals("서울시 강남구 삼성동 123-45 (테헤란로)", address.value)
    }

    @Test
    fun `should throw InvalidShopAddressException when address is blank`() {
        assertFailsWith<InvalidShopAddressException> {
            ShopAddress.of("   ")
        }
    }

    @Test
    fun `should throw InvalidShopAddressException when address is empty`() {
        assertFailsWith<InvalidShopAddressException> {
            ShopAddress.of("")
        }
    }

    @Test
    fun `should throw InvalidShopAddressException when address is too short`() {
        assertFailsWith<InvalidShopAddressException> {
            ShopAddress.of("서울")
        }
    }

    @Test
    fun `should throw InvalidShopAddressException when address is too long`() {
        val tooLong = "A".repeat(201)
        assertFailsWith<InvalidShopAddressException> {
            ShopAddress.of(tooLong)
        }
    }
}
