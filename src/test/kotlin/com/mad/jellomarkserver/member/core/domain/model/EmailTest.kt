package com.mad.jellomarkserver.member.core.domain.model

import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberEmailException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class EmailTest {

    @Test
    fun `should create Email with valid simple email`() {
        val memberEmail = MemberEmail.of("test@example.com")
        assertEquals("test@example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email containing dots`() {
        val memberEmail = MemberEmail.of("user.name@example.com")
        assertEquals("user.name@example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email containing plus`() {
        val memberEmail = MemberEmail.of("user+tag@example.com")
        assertEquals("user+tag@example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email containing underscore`() {
        val memberEmail = MemberEmail.of("user_name@example.com")
        assertEquals("user_name@example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email containing hyphen`() {
        val memberEmail = MemberEmail.of("user-name@example.com")
        assertEquals("user-name@example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email containing percent`() {
        val memberEmail = MemberEmail.of("user%name@example.com")
        assertEquals("user%name@example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email containing numbers`() {
        val memberEmail = MemberEmail.of("user123@example456.com")
        assertEquals("user123@example456.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email with subdomain`() {
        val memberEmail = MemberEmail.of("user@mail.example.com")
        assertEquals("user@mail.example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email with multiple subdomains`() {
        val memberEmail = MemberEmail.of("user@mail.server.example.com")
        assertEquals("user@mail.server.example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email with hyphen in domain`() {
        val memberEmail = MemberEmail.of("user@my-domain.com")
        assertEquals("user@my-domain.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email with two letter TLD`() {
        val memberEmail = MemberEmail.of("user@example.co")
        assertEquals("user@example.co", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email with three letter TLD`() {
        val memberEmail = MemberEmail.of("user@example.com")
        assertEquals("user@example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email with long TLD`() {
        val memberEmail = MemberEmail.of("user@example.technology")
        assertEquals("user@example.technology", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email with uppercase letters`() {
        val memberEmail = MemberEmail.of("User@Example.COM")
        assertEquals("User@Example.COM", memberEmail.value)
    }

    @Test
    fun `should create Email with valid email with mixed case`() {
        val memberEmail = MemberEmail.of("UsEr@ExAmPlE.CoM")
        assertEquals("UsEr@ExAmPlE.CoM", memberEmail.value)
    }

    @Test
    fun `should create Email with very short local part`() {
        val memberEmail = MemberEmail.of("a@example.com")
        assertEquals("a@example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with very short domain`() {
        val memberEmail = MemberEmail.of("user@a.co")
        assertEquals("user@a.co", memberEmail.value)
    }

    @Test
    fun `should create Email with long local part`() {
        val memberEmail = MemberEmail.of("verylonglocalpartnamefortest@example.com")
        assertEquals("verylonglocalpartnamefortest@example.com", memberEmail.value)
    }

    @Test
    fun `should create Email with long domain`() {
        val memberEmail = MemberEmail.of("user@verylongdomainnamefortesting.com")
        assertEquals("user@verylongdomainnamefortesting.com", memberEmail.value)
    }

    @Test
    fun `should trim whitespace before validation`() {
        val memberEmail = MemberEmail.of("  user@example.com  ")
        assertEquals("user@example.com", memberEmail.value)
    }

    @Test
    fun `should throw when email is blank`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("")
        }
    }

    @Test
    fun `should throw when email is only whitespace`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("   ")
        }
    }

    @Test
    fun `should throw when email has no at sign`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("userexample.com")
        }
    }

    @Test
    fun `should throw when email has multiple at signs`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user@@example.com")
        }
    }

    @Test
    fun `should throw when email has no local part`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("@example.com")
        }
    }

    @Test
    fun `should throw when email has no domain`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user@")
        }
    }

    @Test
    fun `should throw when email has no TLD`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user@example")
        }
    }


    @Test
    fun `should throw when email has space in local part`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user name@example.com")
        }
    }

    @Test
    fun `should throw when email has space in domain`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user@exam ple.com")
        }
    }

    @Test
    fun `should throw when email has invalid character in local part`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user#name@example.com")
        }
    }

    @Test
    fun `should throw when email has invalid character in domain`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user@exam_ple.com")
        }
    }

    @Test
    fun `should throw when email has missing dot in domain`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user@examplecom")
        }
    }

    @Test
    fun `should throw when email has dot at end of domain`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user@example.com.")
        }
    }

    @Test
    fun `should throw when email TLD has only one character`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user@example.c")
        }
    }

    @Test
    fun `should throw when email TLD contains numbers`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user@example.c0m")
        }
    }

    @Test
    fun `should throw when email has brackets`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user[name]@example.com")
        }
    }

    @Test
    fun `should throw when email has quotes`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("\"user\"@example.com")
        }
    }

    @Test
    fun `should throw when email has backslash`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user\\name@example.com")
        }
    }

    @Test
    fun `should throw when email starts with at sign`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("@user@example.com")
        }
    }

    @Test
    fun `should throw when email ends with at sign`() {
        assertFailsWith<InvalidMemberEmailException> {
            MemberEmail.of("user@example.com@")
        }
    }
}
