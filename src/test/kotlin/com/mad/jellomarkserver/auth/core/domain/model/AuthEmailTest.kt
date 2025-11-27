package com.mad.jellomarkserver.auth.core.domain.model

import com.mad.jellomarkserver.auth.core.domain.exception.InvalidAuthEmailException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class AuthEmailTest {

    @Test
    fun `should create AuthEmail with valid simple email`() {
        val authEmail = AuthEmail.of("test@example.com")
        assertEquals("test@example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email containing dots`() {
        val authEmail = AuthEmail.of("user.name@example.com")
        assertEquals("user.name@example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email containing plus`() {
        val authEmail = AuthEmail.of("user+tag@example.com")
        assertEquals("user+tag@example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email containing underscore`() {
        val authEmail = AuthEmail.of("user_name@example.com")
        assertEquals("user_name@example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email containing hyphen`() {
        val authEmail = AuthEmail.of("user-name@example.com")
        assertEquals("user-name@example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email containing percent`() {
        val authEmail = AuthEmail.of("user%name@example.com")
        assertEquals("user%name@example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email containing numbers`() {
        val authEmail = AuthEmail.of("user123@example456.com")
        assertEquals("user123@example456.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email with subdomain`() {
        val authEmail = AuthEmail.of("user@mail.example.com")
        assertEquals("user@mail.example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email with multiple subdomains`() {
        val authEmail = AuthEmail.of("user@mail.server.example.com")
        assertEquals("user@mail.server.example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email with hyphen in domain`() {
        val authEmail = AuthEmail.of("user@my-domain.com")
        assertEquals("user@my-domain.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email with two letter TLD`() {
        val authEmail = AuthEmail.of("user@example.co")
        assertEquals("user@example.co", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email with three letter TLD`() {
        val authEmail = AuthEmail.of("user@example.com")
        assertEquals("user@example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email with long TLD`() {
        val authEmail = AuthEmail.of("user@example.technology")
        assertEquals("user@example.technology", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email with uppercase letters`() {
        val authEmail = AuthEmail.of("User@Example.COM")
        assertEquals("User@Example.COM", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with valid email with mixed case`() {
        val authEmail = AuthEmail.of("UsEr@ExAmPlE.CoM")
        assertEquals("UsEr@ExAmPlE.CoM", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with very short local part`() {
        val authEmail = AuthEmail.of("a@example.com")
        assertEquals("a@example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with very short domain`() {
        val authEmail = AuthEmail.of("user@a.co")
        assertEquals("user@a.co", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with long local part`() {
        val authEmail = AuthEmail.of("verylonglocalpartnamefortest@example.com")
        assertEquals("verylonglocalpartnamefortest@example.com", authEmail.value)
    }

    @Test
    fun `should create AuthEmail with long domain`() {
        val authEmail = AuthEmail.of("user@verylongdomainnamefortesting.com")
        assertEquals("user@verylongdomainnamefortesting.com", authEmail.value)
    }

    @Test
    fun `should trim whitespace before validation`() {
        val authEmail = AuthEmail.of("  user@example.com  ")
        assertEquals("user@example.com", authEmail.value)
    }

    @Test
    fun `should throw when email is blank`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("")
        }
    }

    @Test
    fun `should throw when email is only whitespace`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("   ")
        }
    }

    @Test
    fun `should throw when email has no at sign`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("userexample.com")
        }
    }

    @Test
    fun `should throw when email has multiple at signs`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user@@example.com")
        }
    }

    @Test
    fun `should throw when email has no local part`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("@example.com")
        }
    }

    @Test
    fun `should throw when email has no domain`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user@")
        }
    }

    @Test
    fun `should throw when email has no TLD`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user@example")
        }
    }

    @Test
    fun `should throw when email has space in local part`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user name@example.com")
        }
    }

    @Test
    fun `should throw when email has space in domain`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user@exam ple.com")
        }
    }

    @Test
    fun `should throw when email has invalid character in local part`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user#name@example.com")
        }
    }

    @Test
    fun `should throw when email has invalid character in domain`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user@exam_ple.com")
        }
    }

    @Test
    fun `should throw when email has missing dot in domain`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user@examplecom")
        }
    }

    @Test
    fun `should throw when email has dot at end of domain`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user@example.com.")
        }
    }

    @Test
    fun `should throw when email TLD has only one character`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user@example.c")
        }
    }

    @Test
    fun `should throw when email TLD contains numbers`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user@example.c0m")
        }
    }

    @Test
    fun `should throw when email has brackets`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user[name]@example.com")
        }
    }

    @Test
    fun `should throw when email has quotes`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("\"user\"@example.com")
        }
    }

    @Test
    fun `should throw when email has backslash`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user\\name@example.com")
        }
    }

    @Test
    fun `should throw when email starts with at sign`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("@user@example.com")
        }
    }

    @Test
    fun `should throw when email ends with at sign`() {
        assertFailsWith<InvalidAuthEmailException> {
            AuthEmail.of("user@example.com@")
        }
    }
}
