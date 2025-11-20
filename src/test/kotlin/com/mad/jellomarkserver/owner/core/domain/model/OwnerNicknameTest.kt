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
    fun `should create OwnerNickname with valid 3 character nickname`() {
        val nickname = OwnerNickname.of("abc")
        assertEquals("abc", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with valid 4 character nickname`() {
        val nickname = OwnerNickname.of("abcd")
        assertEquals("abcd", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with valid 5 character nickname`() {
        val nickname = OwnerNickname.of("abcde")
        assertEquals("abcde", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with valid 6 character nickname`() {
        val nickname = OwnerNickname.of("abcdef")
        assertEquals("abcdef", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with valid 7 character nickname`() {
        val nickname = OwnerNickname.of("abcdefg")
        assertEquals("abcdefg", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with mixed case letters`() {
        val nickname = OwnerNickname.of("AbCdEf")
        assertEquals("AbCdEf", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with only numbers`() {
        val nickname = OwnerNickname.of("12345678")
        assertEquals("12345678", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with letters and numbers`() {
        val nickname = OwnerNickname.of("abc123")
        assertEquals("abc123", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with underscore`() {
        val nickname = OwnerNickname.of("user_123")
        assertEquals("user_123", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with hyphen`() {
        val nickname = OwnerNickname.of("user-123")
        assertEquals("user-123", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with dot`() {
        val nickname = OwnerNickname.of("user.123")
        assertEquals("user.123", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with special characters`() {
        val nickname = OwnerNickname.of("@#$%^&*(")
        assertEquals("@#$%^&*(", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with Korean characters`() {
        val nickname = OwnerNickname.of("한글닉")
        assertEquals("한글닉", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with Japanese characters`() {
        val nickname = OwnerNickname.of("ユーザー名")
        assertEquals("ユーザー名", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with Chinese characters`() {
        val nickname = OwnerNickname.of("用户名")
        assertEquals("用户名", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with emoji`() {
        val nickname = OwnerNickname.of("😀😁")
        assertEquals("😀😁", nickname.value)
    }

    @Test
    fun `should create OwnerNickname with mixed Korean and English`() {
        val nickname = OwnerNickname.of("유저123")
        assertEquals("유저123", nickname.value)
    }

    @Test
    fun `should trim whitespace before validation`() {
        val nickname = OwnerNickname.of("  ab  ")
        assertEquals("ab", nickname.value)
    }

    @Test
    fun `should create OwnerNickname after trimming to exactly 2 characters`() {
        val nickname = OwnerNickname.of("  ab  ")
        assertEquals("ab", nickname.value)
    }

    @Test
    fun `should create OwnerNickname after trimming to exactly 8 characters`() {
        val nickname = OwnerNickname.of("  12345678  ")
        assertEquals("12345678", nickname.value)
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

    @Test
    fun `should throw when nickname contains carriage return`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("ab\rcd")
        }
    }

    @Test
    fun `should throw when nickname has 10 characters`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("abcdefghij")
        }
    }

    @Test
    fun `should throw when nickname is too long`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("verylongnickname")
        }
    }

    @Test
    fun `should throw when nickname after trimming is too short`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("  a  ")
        }
    }

    @Test
    fun `should throw when nickname after trimming is too long`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("  123456789  ")
        }
    }

    @Test
    fun `should throw when nickname after trimming contains space`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("  ab cd  ")
        }
    }

    @Test
    fun `should throw when nickname contains space in middle`() {
        assertFailsWith<InvalidOwnerNicknameException> {
            OwnerNickname.of("user name")
        }
    }
}
