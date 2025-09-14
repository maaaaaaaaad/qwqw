package com.mad.jellomarkserver.member.domain.model

import com.mad.jellomarkserver.member.domain.model.Nickname
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NicknameTest {
    @Test
    fun `Accept minimum length 2`() {
        val n = Nickname.of("ab")
        assertEquals("ab", n.value)
    }

    @Test
    fun `Accept maximum length 8`() {
        val n = Nickname.of("abcdefgh")
        assertEquals("abcdefgh", n.value)
    }

    @Test
    fun `Valid after trimming`() {
        val n = Nickname.of("  ab  ")
        assertEquals("ab", n.value)
    }

    @Test
    fun `Reject length 1`() {
        assertFailsWith<IllegalArgumentException> { Nickname.of("a") }
    }

    @Test
    fun `Reject length 9`() {
        assertFailsWith<IllegalArgumentException> { Nickname.of("abcdefghi") }
    }

    @Test
    fun `Reject if contains whitespace`() {
        assertFailsWith<IllegalArgumentException> { Nickname.of("a b") }
    }

    @Test
    fun `Reject null and blank`() {
        assertFailsWith<IllegalArgumentException> { Nickname.of(null) }
        assertFailsWith<IllegalArgumentException> { Nickname.of("") }
        assertFailsWith<IllegalArgumentException> { Nickname.of("   ") }
    }
}
