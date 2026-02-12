package com.mad.jellomarkserver.review.core.domain.model

import com.mad.jellomarkserver.review.core.domain.exception.InvalidReplyContentException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ReplyContentTest {

    @Test
    fun `should create reply content with valid text`() {
        val replyContent = ReplyContent.of("감사합니다!")

        assertEquals("감사합니다!", replyContent.value)
    }

    @Test
    fun `should trim whitespace`() {
        val replyContent = ReplyContent.of("  감사합니다!  ")

        assertEquals("감사합니다!", replyContent.value)
    }

    @Test
    fun `should create reply content with 1 character`() {
        val replyContent = ReplyContent.of("감")

        assertEquals("감", replyContent.value)
    }

    @Test
    fun `should create reply content with 500 characters`() {
        val longContent = "가".repeat(500)
        val replyContent = ReplyContent.of(longContent)

        assertEquals(500, replyContent.value.length)
    }

    @Test
    fun `should throw exception for empty content`() {
        assertFailsWith<InvalidReplyContentException> {
            ReplyContent.of("")
        }
    }

    @Test
    fun `should throw exception for blank content`() {
        assertFailsWith<InvalidReplyContentException> {
            ReplyContent.of("   ")
        }
    }

    @Test
    fun `should throw exception for content exceeding 500 characters`() {
        val tooLong = "가".repeat(501)

        assertFailsWith<InvalidReplyContentException> {
            ReplyContent.of(tooLong)
        }
    }
}
