package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopPhoneNumberException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ShopPhoneNumberTest {

    @Test
    fun `should create ShopPhoneNumber with valid mobile number`() {
        val phoneNumber = ShopPhoneNumber.of("010-1234-5678")
        assertEquals("010-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create ShopPhoneNumber with valid Seoul landline`() {
        val phoneNumber = ShopPhoneNumber.of("02-123-4567")
        assertEquals("02-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create ShopPhoneNumber with valid regional landline`() {
        val phoneNumber = ShopPhoneNumber.of("031-123-4567")
        assertEquals("031-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create ShopPhoneNumber with 4-digit middle part`() {
        val phoneNumber = ShopPhoneNumber.of("02-1234-5678")
        assertEquals("02-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should trim whitespace from phone number`() {
        val phoneNumber = ShopPhoneNumber.of("  010-1234-5678  ")
        assertEquals("010-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should throw InvalidShopPhoneNumberException when number is blank`() {
        assertFailsWith<InvalidShopPhoneNumberException> {
            ShopPhoneNumber.of("   ")
        }
    }

    @Test
    fun `should throw InvalidShopPhoneNumberException when number is empty`() {
        assertFailsWith<InvalidShopPhoneNumberException> {
            ShopPhoneNumber.of("")
        }
    }

    @Test
    fun `should throw InvalidShopPhoneNumberException when number has no hyphens`() {
        assertFailsWith<InvalidShopPhoneNumberException> {
            ShopPhoneNumber.of("01012345678")
        }
    }

    @Test
    fun `should throw InvalidShopPhoneNumberException when number has invalid format`() {
        assertFailsWith<InvalidShopPhoneNumberException> {
            ShopPhoneNumber.of("010-12-345678")
        }
    }

    @Test
    fun `should throw InvalidShopPhoneNumberException when area code is invalid`() {
        assertFailsWith<InvalidShopPhoneNumberException> {
            ShopPhoneNumber.of("020-1234-5678")
        }
    }
}
