package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidBusinessNumberException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class BusinessNumberTest {

    @Test
    fun `should create BusinessNumber with valid 9 digit number`() {
        val businessNumber = BusinessNumber.of("123456789")
        assertEquals("123456789", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with valid 9 digit number starting with 1`() {
        val businessNumber = BusinessNumber.of("101234567")
        assertEquals("101234567", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with valid 9 digit number starting with 2`() {
        val businessNumber = BusinessNumber.of("234567890")
        assertEquals("234567890", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with valid 9 digit number starting with 9`() {
        val businessNumber = BusinessNumber.of("987654321")
        assertEquals("987654321", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with all zeros`() {
        val businessNumber = BusinessNumber.of("000000000")
        assertEquals("000000000", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with all nines`() {
        val businessNumber = BusinessNumber.of("999999999")
        assertEquals("999999999", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with 9 alphanumeric characters`() {
        val businessNumber = BusinessNumber.of("abc123def")
        assertEquals("abc123def", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with 9 uppercase letters`() {
        val businessNumber = BusinessNumber.of("ABCDEFGHI")
        assertEquals("ABCDEFGHI", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with 9 lowercase letters`() {
        val businessNumber = BusinessNumber.of("abcdefghi")
        assertEquals("abcdefghi", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with 9 mixed case letters`() {
        val businessNumber = BusinessNumber.of("AbCdEfGhI")
        assertEquals("AbCdEfGhI", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with 9 special characters`() {
        val businessNumber = BusinessNumber.of("!@#$%^&*(")
        assertEquals("!@#$%^&*(", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with hyphens`() {
        val businessNumber = BusinessNumber.of("123-45-67")
        assertEquals("123-45-67", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with dots`() {
        val businessNumber = BusinessNumber.of("123.456.7")
        assertEquals("123.456.7", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with mixed special characters`() {
        val businessNumber = BusinessNumber.of("12-34.567")
        assertEquals("12-34.567", businessNumber.value)
    }

    @Test
    fun `should trim whitespace before validation`() {
        val businessNumber = BusinessNumber.of("  123456789  ")
        assertEquals("123456789", businessNumber.value)
    }

    @Test
    fun `should trim leading whitespace before validation`() {
        val businessNumber = BusinessNumber.of("   123456789")
        assertEquals("123456789", businessNumber.value)
    }

    @Test
    fun `should trim trailing whitespace before validation`() {
        val businessNumber = BusinessNumber.of("123456789   ")
        assertEquals("123456789", businessNumber.value)
    }

    @Test
    fun `should trim tab characters before validation`() {
        val businessNumber = BusinessNumber.of("\t123456789\t")
        assertEquals("123456789", businessNumber.value)
    }

    @Test
    fun `should trim newline characters before validation`() {
        val businessNumber = BusinessNumber.of("\n123456789\n")
        assertEquals("123456789", businessNumber.value)
    }

    @Test
    fun `should throw when business number is blank`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("")
        }
    }

    @Test
    fun `should throw when business number is only whitespace`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("   ")
        }

    }

    @Test
    fun `should throw when business number is only tab`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("\t")
        }
    }

    @Test
    fun `should throw when business number is only newline`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("\n")
        }
    }

    @Test
    fun `should throw when business number has 1 character`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("1")
        }
    }

    @Test
    fun `should throw when business number has 2 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("12")
        }
    }

    @Test
    fun `should throw when business number has 3 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("123")
        }
    }

    @Test
    fun `should throw when business number has 4 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("1234")
        }
    }

    @Test
    fun `should throw when business number has 5 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("12345")
        }
    }

    @Test
    fun `should throw when business number has 6 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("123456")
        }
    }

    @Test
    fun `should throw when business number has 7 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("1234567")
        }
    }

    @Test
    fun `should throw when business number has 8 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("12345678")
        }
    }

    @Test
    fun `should throw when business number has 10 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("1234567890")
        }
    }

    @Test
    fun `should throw when business number has 11 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("12345678901")
        }
    }

    @Test
    fun `should throw when business number has 12 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("123456789012")
        }
    }

    @Test
    fun `should throw when business number has 15 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("123456789012345")
        }
    }

    @Test
    fun `should throw when business number has 20 characters`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("12345678901234567890")
        }
    }

    @Test
    fun `should throw when trimmed business number is too short after removing whitespace`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("  12345678  ")
        }
    }

    @Test
    fun `should throw when trimmed business number is too long after removing whitespace`() {
        assertFailsWith<InvalidBusinessNumberException> {
            BusinessNumber.of("  1234567890  ")
        }
    }
}
