package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerBusinessNumberException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class BusinessNumberTest {

    @Test
    fun `should create BusinessNumber with valid 10 digit number`() {
        val businessNumber = BusinessNumber.of("1234567890")
        assertEquals("1234567890", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with valid 10 digit number starting with 1`() {
        val businessNumber = BusinessNumber.of("1012345670")
        assertEquals("1012345670", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with valid 10 digit number starting with 2`() {
        val businessNumber = BusinessNumber.of("2345678901")
        assertEquals("2345678901", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with valid 10 digit number starting with 9`() {
        val businessNumber = BusinessNumber.of("9876543210")
        assertEquals("9876543210", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with all zeros`() {
        val businessNumber = BusinessNumber.of("0000000000")
        assertEquals("0000000000", businessNumber.value)
    }

    @Test
    fun `should create BusinessNumber with all nines`() {
        val businessNumber = BusinessNumber.of("9999999999")
        assertEquals("9999999999", businessNumber.value)
    }

    @Test
    fun `should strip hyphens and store digits only`() {
        val businessNumber = BusinessNumber.of("123-45-67890")
        assertEquals("1234567890", businessNumber.value)
    }

    @Test
    fun `should strip hyphens in standard format`() {
        val businessNumber = BusinessNumber.of("000-00-00000")
        assertEquals("0000000000", businessNumber.value)
    }

    @Test
    fun `should trim whitespace before validation`() {
        val businessNumber = BusinessNumber.of("  1234567890  ")
        assertEquals("1234567890", businessNumber.value)
    }

    @Test
    fun `should trim leading whitespace before validation`() {
        val businessNumber = BusinessNumber.of("   1234567890")
        assertEquals("1234567890", businessNumber.value)
    }

    @Test
    fun `should trim trailing whitespace before validation`() {
        val businessNumber = BusinessNumber.of("1234567890   ")
        assertEquals("1234567890", businessNumber.value)
    }

    @Test
    fun `should trim tab characters before validation`() {
        val businessNumber = BusinessNumber.of("\t1234567890\t")
        assertEquals("1234567890", businessNumber.value)
    }

    @Test
    fun `should trim newline characters before validation`() {
        val businessNumber = BusinessNumber.of("\n1234567890\n")
        assertEquals("1234567890", businessNumber.value)
    }

    @Test
    fun `should throw when business number is blank`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("")
        }
    }

    @Test
    fun `should throw when business number is only whitespace`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("   ")
        }

    }

    @Test
    fun `should throw when business number is only tab`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("\t")
        }
    }

    @Test
    fun `should throw when business number is only newline`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("\n")
        }
    }

    @Test
    fun `should throw when business number has 1 digit`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("1")
        }
    }

    @Test
    fun `should throw when business number has 5 digits`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("12345")
        }
    }

    @Test
    fun `should throw when business number has 8 digits`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("12345678")
        }
    }

    @Test
    fun `should throw when business number has 9 digits`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("123456789")
        }
    }

    @Test
    fun `should throw when business number has 11 digits`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("12345678901")
        }
    }

    @Test
    fun `should throw when business number has 12 digits`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("123456789012")
        }
    }

    @Test
    fun `should throw when business number has 15 digits`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("123456789012345")
        }
    }

    @Test
    fun `should throw when business number contains letters`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("abc1234567")
        }
    }

    @Test
    fun `should throw when business number contains uppercase letters`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("ABCDEFGHIJ")
        }
    }

    @Test
    fun `should throw when business number contains special characters`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("!@#$%^&*()")
        }
    }

    @Test
    fun `should throw when business number contains dots`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("123.456.7890")
        }
    }

    @Test
    fun `should throw when trimmed business number is too short after removing whitespace`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("  123456789  ")
        }
    }
}
