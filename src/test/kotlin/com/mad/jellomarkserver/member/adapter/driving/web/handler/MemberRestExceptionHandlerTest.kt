package com.mad.jellomarkserver.member.adapter.driving.web.handler

import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberEmailException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class MemberRestExceptionHandlerTest {

    private val handler = MemberRestExceptionHandler()

    @Test
    fun `should handle DuplicateMemberEmailException with CONFLICT status`() {
        val exception = DuplicateMemberEmailException("test@example.com")

        val result = handler.handleDuplicateEmail(exception)

        assertEquals(HttpStatus.CONFLICT.value(), result.status)
        assertEquals("Email already in use: test@example.com", result.detail)
    }
}
