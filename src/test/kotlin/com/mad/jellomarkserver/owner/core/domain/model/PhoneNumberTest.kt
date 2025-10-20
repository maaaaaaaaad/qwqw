package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidPhoneNumberException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class PhoneNumberTest {

    @Test
    fun `should create PhoneNumber with valid 010 mobile number`() {
        val phoneNumber = PhoneNumber.of("010-1234-5678")
        assertEquals("010-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid 010 mobile number with 3 digit middle part`() {
        val phoneNumber = PhoneNumber.of("010-123-4567")
        assertEquals("010-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid 011 mobile number`() {
        val phoneNumber = PhoneNumber.of("011-1234-5678")
        assertEquals("011-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid 016 mobile number`() {
        val phoneNumber = PhoneNumber.of("016-9999-8888")
        assertEquals("016-9999-8888", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid 017 mobile number`() {
        val phoneNumber = PhoneNumber.of("017-123-4567")
        assertEquals("017-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid 018 mobile number`() {
        val phoneNumber = PhoneNumber.of("018-1234-5678")
        assertEquals("018-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid 019 mobile number`() {
        val phoneNumber = PhoneNumber.of("019-1234-5678")
        assertEquals("019-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Seoul 02 number with 3 digit middle part`() {
        val phoneNumber = PhoneNumber.of("02-123-4567")
        assertEquals("02-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Seoul 02 number with 4 digit middle part`() {
        val phoneNumber = PhoneNumber.of("02-1234-5678")
        assertEquals("02-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Gyeonggi 031 number`() {
        val phoneNumber = PhoneNumber.of("031-123-4567")
        assertEquals("031-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Incheon 032 number`() {
        val phoneNumber = PhoneNumber.of("032-1234-5678")
        assertEquals("032-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Gangwon 033 number`() {
        val phoneNumber = PhoneNumber.of("033-123-4567")
        assertEquals("033-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Chungnam 041 number`() {
        val phoneNumber = PhoneNumber.of("041-1234-5678")
        assertEquals("041-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Daejeon 042 number`() {
        val phoneNumber = PhoneNumber.of("042-123-4567")
        assertEquals("042-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Chungbuk 043 number`() {
        val phoneNumber = PhoneNumber.of("043-1234-5678")
        assertEquals("043-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Sejong 044 number`() {
        val phoneNumber = PhoneNumber.of("044-123-4567")
        assertEquals("044-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Busan 051 number`() {
        val phoneNumber = PhoneNumber.of("051-1234-5678")
        assertEquals("051-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Ulsan 052 number`() {
        val phoneNumber = PhoneNumber.of("052-123-4567")
        assertEquals("052-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Daegu 053 number`() {
        val phoneNumber = PhoneNumber.of("053-1234-5678")
        assertEquals("053-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Gyeongnam 055 number`() {
        val phoneNumber = PhoneNumber.of("055-123-4567")
        assertEquals("055-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Jeonbuk 063 number`() {
        val phoneNumber = PhoneNumber.of("063-1234-5678")
        assertEquals("063-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Gwangju 062 number`() {
        val phoneNumber = PhoneNumber.of("062-123-4567")
        assertEquals("062-123-4567", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Jeonnam 061 number`() {
        val phoneNumber = PhoneNumber.of("061-1234-5678")
        assertEquals("061-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should create PhoneNumber with valid Jeju 064 number`() {
        val phoneNumber = PhoneNumber.of("064-123-4567")
        assertEquals("064-123-4567", phoneNumber.value)
    }

    @Test
    fun `should trim whitespace before validation`() {
        val phoneNumber = PhoneNumber.of("  010-1234-5678  ")
        assertEquals("010-1234-5678", phoneNumber.value)
    }

    @Test
    fun `should throw when phone number is blank`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("")
        }
    }

    @Test
    fun `should throw when phone number is only whitespace`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("   ")
        }
    }

    @Test
    fun `should throw when phone number has no hyphens`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("01012345678")
        }
    }

    @Test
    fun `should throw when phone number has invalid area code 012`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("012-1234-5678")
        }
    }

    @Test
    fun `should throw when phone number has invalid area code 015`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("015-1234-5678")
        }
    }

    @Test
    fun `should throw when phone number has invalid regional code 034`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("034-1234-5678")
        }
    }

    @Test
    fun `should throw when phone number has invalid regional code 056`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("056-1234-5678")
        }
    }

    @Test
    fun `should throw when middle part has too few digits`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("010-12-5678")
        }
    }

    @Test
    fun `should throw when middle part has too many digits`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("010-12345-5678")
        }
    }

    @Test
    fun `should throw when last part has too few digits`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("010-1234-567")
        }
    }

    @Test
    fun `should throw when last part has too many digits`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("010-1234-56789")
        }
    }

    @Test
    fun `should throw when phone number contains non-digit characters`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("010-abcd-5678")
        }
    }

    @Test
    fun `should throw when phone number has wrong format with dots`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("010.1234.5678")
        }
    }

    @Test
    fun `should throw when phone number has wrong format with spaces`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("010 1234 5678")
        }
    }

    @Test
    fun `should throw when phone number starts with 1`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("1234-5678")
        }
    }

    @Test
    fun `should throw when phone number is international format`() {
        assertFailsWith<InvalidPhoneNumberException> {
            PhoneNumber.of("+82-10-1234-5678")
        }
    }
}
