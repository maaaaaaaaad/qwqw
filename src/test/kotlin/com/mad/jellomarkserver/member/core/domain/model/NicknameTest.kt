package com.mad.jellomarkserver.member.core.domain.model

import com.mad.jellomarkserver.member.core.domain.exception.InvalidNicknameException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class NicknameTest {

    @Test
    fun `should create Nickname with valid 2 character nickname`() {
        val nickname = Nickname.of("ab")
        assertEquals("ab", nickname.value)
    }

    @Test
    fun `should create Nickname with valid 3 character nickname`() {
        val nickname = Nickname.of("abc")
        assertEquals("abc", nickname.value)
    }

    @Test
    fun `should create Nickname with valid 4 character nickname`() {
        val nickname = Nickname.of("abcd")
        assertEquals("abcd", nickname.value)
    }

    @Test
    fun `should create Nickname with valid 5 character nickname`() {
        val nickname = Nickname.of("abcde")
        assertEquals("abcde", nickname.value)
    }

    @Test
    fun `should create Nickname with valid 6 character nickname`() {
        val nickname = Nickname.of("abcdef")
        assertEquals("abcdef", nickname.value)
    }

    @Test
    fun `should create Nickname with valid 7 character nickname`() {
        val nickname = Nickname.of("abcdefg")
        assertEquals("abcdefg", nickname.value)
    }

    @Test
    fun `should create Nickname with valid 8 character nickname`() {
        val nickname = Nickname.of("abcdefgh")
        assertEquals("abcdefgh", nickname.value)
    }

    @Test
    fun `should create Nickname with uppercase letters`() {
        val nickname = Nickname.of("ABCD")
        assertEquals("ABCD", nickname.value)
    }

    @Test
    fun `should create Nickname with mixed case letters`() {
        val nickname = Nickname.of("AbCdEf")
        assertEquals("AbCdEf", nickname.value)
    }

    @Test
    fun `should create Nickname with numbers`() {
        val nickname = Nickname.of("user123")
        assertEquals("user123", nickname.value)
    }

    @Test
    fun `should create Nickname with only numbers`() {
        val nickname = Nickname.of("12345678")
        assertEquals("12345678", nickname.value)
    }

    @Test
    fun `should create Nickname with letters and numbers`() {
        val nickname = Nickname.of("abc123")
        assertEquals("abc123", nickname.value)
    }

    @Test
    fun `should create Nickname with underscore`() {
        val nickname = Nickname.of("user_123")
        assertEquals("user_123", nickname.value)
    }

    @Test
    fun `should create Nickname with hyphen`() {
        val nickname = Nickname.of("user-123")
        assertEquals("user-123", nickname.value)
    }

    @Test
    fun `should create Nickname with dot`() {
        val nickname = Nickname.of("user.123")
        assertEquals("user.123", nickname.value)
    }

    @Test
    fun `should create Nickname with special characters`() {
        val nickname = Nickname.of("@#$%^&*(")
        assertEquals("@#$%^&*(", nickname.value)
    }

    @Test
    fun `should create Nickname with Korean characters`() {
        val nickname = Nickname.of("한글닉네임")
        assertEquals("한글닉네임", nickname.value)
    }

    @Test
    fun `should create Nickname with Japanese characters`() {
        val nickname = Nickname.of("ユーザー名")
        assertEquals("ユーザー名", nickname.value)
    }

    @Test
    fun `should create Nickname with Chinese characters`() {
        val nickname = Nickname.of("用户名")
        assertEquals("用户名", nickname.value)
    }

    @Test
    fun `should create Nickname with emoji`() {
        val nickname = Nickname.of("😀😁")
        assertEquals("😀😁", nickname.value)
    }

    @Test
    fun `should create Nickname with mixed Korean and English`() {
        val nickname = Nickname.of("유저123")
        assertEquals("유저123", nickname.value)
    }

    @Test
    fun `should trim whitespace before validation`() {
        val nickname = Nickname.of("  user12  ")
        assertEquals("user12", nickname.value)
    }

    @Test
    fun `should create Nickname after trimming to exactly 2 characters`() {
        val nickname = Nickname.of("  ab  ")
        assertEquals("ab", nickname.value)
    }

    @Test
    fun `should create Nickname after trimming to exactly 8 characters`() {
        val nickname = Nickname.of("  12345678  ")
        assertEquals("12345678", nickname.value)
    }

    @Test
    fun `should throw when nickname is blank`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("")
        }
    }

    @Test
    fun `should throw when nickname is only whitespace`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("   ")
        }
    }

    @Test
    fun `should throw when nickname is only one character`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("a")
        }
    }

    @Test
    fun `should throw when nickname is 9 characters`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("abcdefghi")
        }
    }

    @Test
    fun `should throw when nickname is 10 characters`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("abcdefghij")
        }
    }

    @Test
    fun `should throw when nickname is too long`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("verylongnickname")
        }
    }

    @Test
    fun `should throw when nickname contains space in middle`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("user name")
        }
    }

    @Test
    fun `should throw when nickname contains tab character`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("user\tname")
        }
    }

    @Test
    fun `should throw when nickname contains newline character`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("user\nname")
        }
    }

    @Test
    fun `should throw when nickname contains carriage return`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("user\rname")
        }
    }

    @Test
    fun `should throw when nickname after trimming is too short`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("  a  ")
        }
    }

    @Test
    fun `should throw when nickname after trimming is too long`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("  123456789  ")
        }
    }

    @Test
    fun `should throw when nickname after trimming contains space`() {
        assertFailsWith<InvalidNicknameException> {
            Nickname.of("  ab cd  ")
        }
    }
}
