package com.mad.jellomarkserver.domain.model

import com.mad.jellomarkserver.domain.model.member.Email
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EmailTest {
    @Test
    fun `Accept valid email`() {
        val e = Email.of("user.name+tag@example-domain.com")
        assertEquals("user.name+tag@example-domain.com", e.value)
    }

    @Test
    fun `Valid after trimming`() {
        val e = Email.of("  a@b.co  ")
        assertEquals("a@b.co", e.value)
    }

    @Test
    fun `Reject invalid email`() {
        assertFailsWith<IllegalArgumentException> { Email.of("a@b") }
        assertFailsWith<IllegalArgumentException> { Email.of("@example.com") }
        assertFailsWith<IllegalArgumentException> { Email.of("user@@example.com") }
        assertFailsWith<IllegalArgumentException> { Email.of("user example.com") }
        assertFailsWith<IllegalArgumentException> { Email.of("user@.com") }
    }

    @Test
    fun `Reject null and blank`() {
        assertFailsWith<IllegalArgumentException> { Email.of(null) }
        assertFailsWith<IllegalArgumentException> { Email.of("") }
        assertFailsWith<IllegalArgumentException> { Email.of("   ") }
    }
}
