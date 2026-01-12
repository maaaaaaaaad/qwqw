package com.mad.jellomarkserver.auth.core.domain.model

import com.mad.jellomarkserver.auth.core.domain.exception.InvalidRawPasswordException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class RawPasswordTest {

    @Test
    fun `should create RawPassword with minimum valid password`() {
        val password = RawPassword.of("Abcd123!")
        assertEquals("Abcd123!", password.value)
    }

    @Test
    fun `should create RawPassword with maximum length password`() {
        val password = RawPassword.of("A1!" + "a".repeat(69))
        assertEquals(72, password.value.length)
    }

    @Test
    fun `should create RawPassword with all character types`() {
        val password = RawPassword.of("MyP@ssw0rd!")
        assertEquals("MyP@ssw0rd!", password.value)
    }

    @Test
    fun `should create RawPassword with multiple uppercase letters`() {
        val password = RawPassword.of("ABCDa234!")
        assertEquals("ABCDa234!", password.value)
    }

    @Test
    fun `should create RawPassword with multiple lowercase letters`() {
        val password = RawPassword.of("abcdEFG1!")
        assertEquals("abcdEFG1!", password.value)
    }

    @Test
    fun `should create RawPassword with multiple digits`() {
        val password = RawPassword.of("Pass123456!")
        assertEquals("Pass123456!", password.value)
    }

    @Test
    fun `should create RawPassword with multiple special characters`() {
        val password = RawPassword.of("P@ssw0rd!#")
        assertEquals("P@ssw0rd!#", password.value)
    }

    @Test
    fun `should create RawPassword with various special characters`() {
        val password = RawPassword.of("P@ss#w0rd\$")
        assertEquals("P@ss#w0rd\$", password.value)
    }

    @Test
    fun `should create RawPassword with exclamation mark`() {
        val password = RawPassword.of("Passw0rd!")
        assertEquals("Passw0rd!", password.value)
    }

    @Test
    fun `should create RawPassword with at sign`() {
        val password = RawPassword.of("P@ssw0rd1")
        assertEquals("P@ssw0rd1", password.value)
    }

    @Test
    fun `should create RawPassword with hash`() {
        val password = RawPassword.of("Passw0rd#")
        assertEquals("Passw0rd#", password.value)
    }

    @Test
    fun `should create RawPassword with dollar sign`() {
        val password = RawPassword.of("Passw0rd\$")
        assertEquals("Passw0rd\$", password.value)
    }

    @Test
    fun `should create RawPassword with percent`() {
        val password = RawPassword.of("Passw0rd%")
        assertEquals("Passw0rd%", password.value)
    }

    @Test
    fun `should create RawPassword with ampersand`() {
        val password = RawPassword.of("Passw0rd&")
        assertEquals("Passw0rd&", password.value)
    }

    @Test
    fun `should create RawPassword with asterisk`() {
        val password = RawPassword.of("Passw0rd*")
        assertEquals("Passw0rd*", password.value)
    }

    @Test
    fun `should throw when password is empty`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("")
        }
    }

    @Test
    fun `should throw when password is blank`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("   ")
        }
    }

    @Test
    fun `should throw when password has 7 characters`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("Abc123!")
        }
    }

    @Test
    fun `should throw when password has 1 character`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("A")
        }
    }

    @Test
    fun `should throw when password has 73 characters`() {
        val longPassword = "A1!" + "a".repeat(70)
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of(longPassword)
        }
    }

    @Test
    fun `should throw when password has 100 characters`() {
        val longPassword = "A1!" + "a".repeat(97)
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of(longPassword)
        }
    }

    @Test
    fun `should throw when password has no uppercase letter`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("abcd1234!")
        }
    }

    @Test
    fun `should throw when password has no lowercase letter`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("ABCD1234!")
        }
    }

    @Test
    fun `should throw when password has no digit`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("Abcdefgh!")
        }
    }

    @Test
    fun `should throw when password has no special character`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("Abcd1234")
        }
    }

    @Test
    fun `should throw when password has only uppercase letters`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("ABCDEFGH")
        }
    }

    @Test
    fun `should throw when password has only lowercase letters`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("abcdefgh")
        }
    }

    @Test
    fun `should throw when password has only digits`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("12345678")
        }
    }

    @Test
    fun `should throw when password has only special characters`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("!@#$%^&*")
        }
    }

    @Test
    fun `should throw when password has uppercase and lowercase only`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("AbCdEfGh")
        }
    }

    @Test
    fun `should throw when password has uppercase and digits only`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("ABCD1234")
        }
    }

    @Test
    fun `should throw when password has uppercase and special chars only`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("ABCD!@#$")
        }
    }

    @Test
    fun `should throw when password has lowercase and digits only`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("abcd1234")
        }
    }

    @Test
    fun `should throw when password has lowercase and special chars only`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("abcd!@#$")
        }
    }

    @Test
    fun `should throw when password has digits and special chars only`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("1234!@#$")
        }
    }

    @Test
    fun `should throw when password has uppercase lowercase and digits only`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("Abcd1234")
        }
    }

    @Test
    fun `should throw when password has uppercase lowercase and special chars only`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("Abcd!@#$")
        }
    }

    @Test
    fun `should throw when password has uppercase digits and special chars only`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("ABCD123!")
        }
    }

    @Test
    fun `should throw when password has lowercase digits and special chars only`() {
        assertFailsWith<InvalidRawPasswordException> {
            RawPassword.of("abcd123!")
        }
    }
}
