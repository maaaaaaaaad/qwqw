package com.mad.jellomarkserver.member.adapter.driving.web.handler

import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
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

    @Test
    fun `should handle InvalidMemberEmailException with UNPROCESSABLE_ENTITY status`() {
        val exception = InvalidMemberEmailException("invalid-email")

        val result = handler.handleInvalidEmail(exception)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.status)
        assertEquals("Invalid email invalid-email", result.detail)
    }

    @Test
    fun `should handle DuplicateMemberNicknameException with CONFLICT status`() {
        val exception = DuplicateMemberNicknameException("testuser")

        val result = handler.handleDuplicateMemberNickname(exception)

        assertEquals(HttpStatus.CONFLICT.value(), result.status)
        assertEquals("Nickname already in use: testuser", result.detail)
    }

    @Test
    fun `should handle InvalidMemberNicknameException with UNPROCESSABLE_ENTITY status`() {
        val exception = InvalidMemberNicknameException("a")

        val result = handler.handleInvalidNickname(exception)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.status)
        assertEquals("Invalid nickname: a", result.detail)
    }

    @Test
    fun `should handle generic Exception with INTERNAL_SERVER_ERROR status`() {
        val exception = Exception("Unexpected error")

        val result = handler.handleGeneric(exception)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        assertEquals("INTERNAL_SERVER_ERROR", result.body?.code)
        assertEquals("Unexpected server error", result.body?.message)
    }
}
