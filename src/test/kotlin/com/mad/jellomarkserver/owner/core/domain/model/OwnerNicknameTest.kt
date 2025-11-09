package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerNicknameException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class OwnerNicknameTest {

    @Test
    fun `should create OwnerNickname with valid 2 character nickname`() {
        val nickname = OwnerNickname.of("ab")
        assertEquals("ab", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with valid 8 character nickname`() {
        val nickname = OwnerNickname.of("abcdefgh")
        assertEquals("abcdefgh", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with uppercase letters`() {
        val nickname = OwnerNickname.of("ABCD")
        assertEquals("ABCD", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with numbers`() {
        val nickname = OwnerNickname.of("user123")
        assertEquals("user123", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with Korean characters`() {
        val nickname = OwnerNickname.of("한글닉")
        assertEquals("한글닉", nickname.value)
    }

    @Test
    fun `should trim whitespace before validation`() {
        val nickname = OwnerNickname.of("  ab  ")
        assertEquals("ab", nickname.value)
    }

    @Test
    fun `should throw when nickname is blank`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("")
        }
    }

    @Test
    fun `should throw when nickname is only whitespace`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("   ")
        }
    }

    @Test
    fun `should throw when nickname has 1 character`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("a")
        }
    }

    @Test
    fun `should throw when nickname has 9 characters`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("abcdefghi")
        }
    }

    @Test
    fun `should throw when nickname contains space`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("ab cd")
        }
    }

    @Test
    fun `should throw when nickname contains tab`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("ab\tcd")
        }
    }

    @Test
    fun `should throw when nickname contains newline`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("ab\ncd")
        }
    }
}
