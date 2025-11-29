package com.mad.jellomarkserver.auth.core.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HashedPasswordTest {

    @Test
    fun `should create HashedPassword from RawPassword`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.value.isNotBlank())
    }

    @Test
    fun `should create HashedPassword with BCrypt format`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.value.startsWith("\$2a\$"))
    }

    @Test
    fun `should create HashedPassword with length 60`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertEquals(60, hashedPassword.value.length)
    }

    @Test
    fun `should verify correct password`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should not verify incorrect password`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val wrongPassword = RawPassword.of("Wr0ngP@ss!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertFalse(hashedPassword.matches(wrongPassword))
    }

    @Test
    fun `should produce different hashes for same password`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val hash1 = HashedPassword.fromRaw(rawPassword)
        val hash2 = HashedPassword.fromRaw(rawPassword)
        assertNotEquals(hash1.value, hash2.value)
    }

    @Test
    fun `should verify same password with different hashes`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val hash1 = HashedPassword.fromRaw(rawPassword)
        val hash2 = HashedPassword.fromRaw(rawPassword)
        assertTrue(hash1.matches(rawPassword))
        assertTrue(hash2.matches(rawPassword))
    }

    @Test
    fun `should hash minimum valid password`() {
        val rawPassword = RawPassword.of("Abcd123!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should hash maximum length password`() {
        val rawPassword = RawPassword.of("A1!" + "a".repeat(69))
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should hash password with special characters`() {
        val rawPassword = RawPassword.of("P@ss#w0rd\$")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should not verify password with different case`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val differentCase = RawPassword.of("mYp@SSW0RD!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertFalse(hashedPassword.matches(differentCase))
    }

    @Test
    fun `should not verify password with extra character`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val withExtra = RawPassword.of("MyP@ssw0rd!x")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertFalse(hashedPassword.matches(withExtra))
    }

    @Test
    fun `should not verify password missing character`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val missing = RawPassword.of("MyP@ssw0rd")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertFalse(hashedPassword.matches(missing))
    }

    @Test
    fun `should reconstruct HashedPassword from hash string`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        val reconstructed = HashedPassword.from(hashedPassword.value)
        assertEquals(hashedPassword.value, reconstructed.value)
    }

    @Test
    fun `should verify password with reconstructed hash`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        val reconstructed = HashedPassword.from(hashedPassword.value)
        assertTrue(reconstructed.matches(rawPassword))
    }

    @Test
    fun `should hash password with all uppercase`() {
        val rawPassword = RawPassword.of("ABCDa234!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should hash password with numbers at start`() {
        val rawPassword = RawPassword.of("123Pass@word")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should hash password with numbers at end`() {
        val rawPassword = RawPassword.of("P@ssword123")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should hash password with special chars at start`() {
        val rawPassword = RawPassword.of("!@Pass123word")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should hash password with special chars at end`() {
        val rawPassword = RawPassword.of("Pass123word!@")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should handle multiple verifications of same password`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        assertTrue(hashedPassword.matches(rawPassword))
        assertTrue(hashedPassword.matches(rawPassword))
        assertTrue(hashedPassword.matches(rawPassword))
    }
}
