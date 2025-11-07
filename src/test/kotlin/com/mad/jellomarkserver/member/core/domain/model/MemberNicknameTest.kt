package com.mad.jellomarkserver.member.core.domain.model

import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class MemberNicknameTest {

    @Test
    fun `should create Nickname with valid 2 character nickname`() {
        val memberNickname = MemberNickname.of("ab")
        assertEquals("ab", memberNickname.value)
    }

    @Test
    fun `should create Nickname with valid 3 character nickname`() {
        val memberNickname = MemberNickname.of("abc")
        assertEquals("abc", memberNickname.value)
    }

    @Test
    fun `should create Nickname with valid 4 character nickname`() {
        val memberNickname = MemberNickname.of("abcd")
        assertEquals("abcd", memberNickname.value)
    }

    @Test
    fun `should create Nickname with valid 5 character nickname`() {
        val memberNickname = MemberNickname.of("abcde")
        assertEquals("abcde", memberNickname.value)
    }

    @Test
    fun `should create Nickname with valid 6 character nickname`() {
        val memberNickname = MemberNickname.of("abcdef")
        assertEquals("abcdef", memberNickname.value)
    }

    @Test
    fun `should create Nickname with valid 7 character nickname`() {
        val memberNickname = MemberNickname.of("abcdefg")
        assertEquals("abcdefg", memberNickname.value)
    }

    @Test
    fun `should create Nickname with valid 8 character nickname`() {
        val memberNickname = MemberNickname.of("abcdefgh")
        assertEquals("abcdefgh", memberNickname.value)
    }

    @Test
    fun `should create Nickname with uppercase letters`() {
        val memberNickname = MemberNickname.of("ABCD")
        assertEquals("ABCD", memberNickname.value)
    }

    @Test
    fun `should create Nickname with mixed case letters`() {
        val memberNickname = MemberNickname.of("AbCdEf")
        assertEquals("AbCdEf", memberNickname.value)
    }

    @Test
    fun `should create Nickname with numbers`() {
        val memberNickname = MemberNickname.of("user123")
        assertEquals("user123", memberNickname.value)
    }

    @Test
    fun `should create Nickname with only numbers`() {
        val memberNickname = MemberNickname.of("12345678")
        assertEquals("12345678", memberNickname.value)
    }

    @Test
    fun `should create Nickname with letters and numbers`() {
        val memberNickname = MemberNickname.of("abc123")
        assertEquals("abc123", memberNickname.value)
    }

    @Test
    fun `should create Nickname with underscore`() {
        val memberNickname = MemberNickname.of("user_123")
        assertEquals("user_123", memberNickname.value)
    }

    @Test
    fun `should create Nickname with hyphen`() {
        val memberNickname = MemberNickname.of("user-123")
        assertEquals("user-123", memberNickname.value)
    }

    @Test
    fun `should create Nickname with dot`() {
        val memberNickname = MemberNickname.of("user.123")
        assertEquals("user.123", memberNickname.value)
    }

    @Test
    fun `should create Nickname with special characters`() {
        val memberNickname = MemberNickname.of("@#$%^&*(")
        assertEquals("@#$%^&*(", memberNickname.value)
    }

    @Test
    fun `should create Nickname with Korean characters`() {
        val memberNickname = MemberNickname.of("한글닉네임")
        assertEquals("한글닉네임", memberNickname.value)
    }

    @Test
    fun `should create Nickname with Japanese characters`() {
        val memberNickname = MemberNickname.of("ユーザー名")
        assertEquals("ユーザー名", memberNickname.value)
    }

    @Test
    fun `should create Nickname with Chinese characters`() {
        val memberNickname = MemberNickname.of("用户名")
        assertEquals("用户名", memberNickname.value)
    }

    @Test
    fun `should create Nickname with emoji`() {
        val memberNickname = MemberNickname.of("😀😁")
        assertEquals("😀😁", memberNickname.value)
    }

    @Test
    fun `should create Nickname with mixed Korean and English`() {
        val memberNickname = MemberNickname.of("유저123")
        assertEquals("유저123", memberNickname.value)
    }

    @Test
    fun `should trim whitespace before validation`() {
        val memberNickname = MemberNickname.of("  user12  ")
        assertEquals("user12", memberNickname.value)
    }

    @Test
    fun `should create Nickname after trimming to exactly 2 characters`() {
        val memberNickname = MemberNickname.of("  ab  ")
        assertEquals("ab", memberNickname.value)
    }

    @Test
    fun `should create Nickname after trimming to exactly 8 characters`() {
        val memberNickname = MemberNickname.of("  12345678  ")
        assertEquals("12345678", memberNickname.value)
    }

    @Test
    fun `should throw when nickname is blank`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("")
        }
    }

    @Test
    fun `should throw when nickname is only whitespace`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("   ")
        }
    }

    @Test
    fun `should throw when nickname is only one character`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("a")
        }
    }

    @Test
    fun `should throw when nickname is 9 characters`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("abcdefghi")
        }
    }

    @Test
    fun `should throw when nickname is 10 characters`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("abcdefghij")
        }
    }

    @Test
    fun `should throw when nickname is too long`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("verylongnickname")
        }
    }

    @Test
    fun `should throw when nickname contains space in middle`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("user name")
        }
    }

    @Test
    fun `should throw when nickname contains tab character`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("user\tname")
        }
    }

    @Test
    fun `should throw when nickname contains newline character`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("user\nname")
        }
    }

    @Test
    fun `should throw when nickname contains carriage return`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("user\rname")
        }
    }

    @Test
    fun `should throw when nickname after trimming is too short`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("  a  ")
        }
    }

    @Test
    fun `should throw when nickname after trimming is too long`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("  123456789  ")
        }
    }

    @Test
    fun `should throw when nickname after trimming contains space`() {
        assertFailsWith<InvalidMemberNicknameException> {
            MemberNickname.of("  ab cd  ")
        }
    }
}
