package com.mad.jellomarkserver.member.core.domain.model

import com.mad.jellomarkserver.member.core.domain.exception.InvalidEmailException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class EmailTest {

    @Test
    fun `should create Email with valid simple email`() {
        val email = Email.of("test@example.com")
        assertEquals("test@example.com", email.value)
    }

    @Test
    fun `should create Email with valid email containing dots`() {
        val email = Email.of("user.name@example.com")
        assertEquals("user.name@example.com", email.value)
    }

    @Test
    fun `should create Email with valid email containing plus`() {
        val email = Email.of("user+tag@example.com")
        assertEquals("user+tag@example.com", email.value)
    }

    @Test
    fun `should create Email with valid email containing underscore`() {
        val email = Email.of("user_name@example.com")
        assertEquals("user_name@example.com", email.value)
    }

    @Test
    fun `should create Email with valid email containing hyphen`() {
        val email = Email.of("user-name@example.com")
        assertEquals("user-name@example.com", email.value)
    }

    @Test
    fun `should create Email with valid email containing percent`() {
        val email = Email.of("user%name@example.com")
        assertEquals("user%name@example.com", email.value)
    }

    @Test
    fun `should create Email with valid email containing numbers`() {
        val email = Email.of("user123@example456.com")
        assertEquals("user123@example456.com", email.value)
    }

    @Test
    fun `should create Email with valid email with subdomain`() {
        val email = Email.of("user@mail.example.com")
        assertEquals("user@mail.example.com", email.value)
    }

    @Test
    fun `should create Email with valid email with multiple subdomains`() {
        val email = Email.of("user@mail.server.example.com")
        assertEquals("user@mail.server.example.com", email.value)
    }

    @Test
    fun `should create Email with valid email with hyphen in domain`() {
        val email = Email.of("user@my-domain.com")
        assertEquals("user@my-domain.com", email.value)
    }

    @Test
    fun `should create Email with valid email with two letter TLD`() {
        val email = Email.of("user@example.co")
        assertEquals("user@example.co", email.value)
    }

    @Test
    fun `should create Email with valid email with three letter TLD`() {
        val email = Email.of("user@example.com")
        assertEquals("user@example.com", email.value)
    }

    @Test
    fun `should create Email with valid email with long TLD`() {
        val email = Email.of("user@example.technology")
        assertEquals("user@example.technology", email.value)
    }

    @Test
    fun `should create Email with valid email with uppercase letters`() {
        val email = Email.of("User@Example.COM")
        assertEquals("User@Example.COM", email.value)
    }

    @Test
    fun `should create Email with valid email with mixed case`() {
        val email = Email.of("UsEr@ExAmPlE.CoM")
        assertEquals("UsEr@ExAmPlE.CoM", email.value)
    }

    @Test
    fun `should create Email with very short local part`() {
        val email = Email.of("a@example.com")
        assertEquals("a@example.com", email.value)
    }

    @Test
    fun `should create Email with very short domain`() {
        val email = Email.of("user@a.co")
        assertEquals("user@a.co", email.value)
    }

    @Test
    fun `should create Email with long local part`() {
        val email = Email.of("verylonglocalpartnamefortest@example.com")
        assertEquals("verylonglocalpartnamefortest@example.com", email.value)
    }

    @Test
    fun `should create Email with long domain`() {
        val email = Email.of("user@verylongdomainnamefortesting.com")
        assertEquals("user@verylongdomainnamefortesting.com", email.value)
    }

    @Test
    fun `should trim whitespace before validation`() {
        val email = Email.of("  user@example.com  ")
        assertEquals("user@example.com", email.value)
    }

    @Test
    fun `should throw when email is blank`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("")
        }
    }

    @Test
    fun `should throw when email is only whitespace`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("   ")
        }
    }

    @Test
    fun `should throw when email has no at sign`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("userexample.com")
        }
    }

    @Test
    fun `should throw when email has multiple at signs`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user@@example.com")
        }
    }

    @Test
    fun `should throw when email has no local part`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("@example.com")
        }
    }

    @Test
    fun `should throw when email has no domain`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user@")
        }
    }

    @Test
    fun `should throw when email has no TLD`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user@example")
        }
    }


    @Test
    fun `should throw when email has space in local part`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user name@example.com")
        }
    }

    @Test
    fun `should throw when email has space in domain`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user@exam ple.com")
        }
    }

    @Test
    fun `should throw when email has invalid character in local part`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user#name@example.com")
        }
    }

    @Test
    fun `should throw when email has invalid character in domain`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user@exam_ple.com")
        }
    }

    @Test
    fun `should throw when email has missing dot in domain`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user@examplecom")
        }
    }

    @Test
    fun `should throw when email has dot at end of domain`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user@example.com.")
        }
    }

    @Test
    fun `should throw when email TLD has only one character`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user@example.c")
        }
    }

    @Test
    fun `should throw when email TLD contains numbers`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user@example.c0m")
        }
    }

    @Test
    fun `should throw when email has brackets`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user[name]@example.com")
        }
    }

    @Test
    fun `should throw when email has quotes`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("\"user\"@example.com")
        }
    }

    @Test
    fun `should throw when email has backslash`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user\\name@example.com")
        }
    }

    @Test
    fun `should throw when email starts with at sign`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("@user@example.com")
        }
    }

    @Test
    fun `should throw when email ends with at sign`() {
        assertFailsWith<InvalidEmailException> {
            Email.of("user@example.com@")
        }
    }
}
