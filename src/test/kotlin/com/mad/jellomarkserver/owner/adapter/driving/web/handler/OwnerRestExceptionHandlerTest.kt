package com.mad.jellomarkserver.owner.adapter.driving.web.handler

import com.mad.jellomarkserver.owner.core.domain.exception.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class OwnerRestExceptionHandlerTest {

    private val handler = OwnerRestExceptionHandler()

    @Test
    fun `should handle InvalidOwnerBusinessNumberException with UNPROCESSABLE_ENTITY status`() {
        val exception = InvalidOwnerBusinessNumberException("12345678")

        val result = handler.handleInvalidBusinessNumber(exception)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.status)
        assertEquals("Invalid Business number: 12345678", result.detail)
    }

    @Test
    fun `should handle InvalidOwnerPhoneNumberException with UNPROCESSABLE_ENTITY status`() {
        val exception = InvalidOwnerPhoneNumberException("invalid-phone")

        val result = handler.handleInvalidPhoneNumber(exception)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.status)
        assertEquals("Invalid phone number: invalid-phone", result.detail)
    }

    @Test
    fun `should handle DuplicateOwnerPhoneNumberException with CONFLICT status`() {
        val exception = DuplicateOwnerPhoneNumberException("010-1234-5678")

        val result = handler.handleDuplicatePhoneNumber(exception)

        assertEquals(HttpStatus.CONFLICT.value(), result.status)
        assertEquals("Duplicate phone number: 010-1234-5678", result.detail)
    }

    @Test
    fun `should handle DuplicateOwnerBusinessNumberException with CONFLICT status`() {
        val exception = DuplicateOwnerBusinessNumberException("123456789")

        val result = handler.handleDuplicateBusinessNumber(exception)

        assertEquals(HttpStatus.CONFLICT.value(), result.status)
        assertEquals("Duplicate business number: 123456789", result.detail)
    }

    @Test
    fun `should handle InvalidOwnerNicknameException with UNPROCESSABLE_ENTITY status`() {
        val exception = InvalidOwnerNicknameException("ab")

        val result = handler.handleInvalidNickname(exception)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.status)
        assertEquals("Invalid nickname: ab", result.detail)
    }

    @Test
    fun `should handle generic Exception with INTERNAL_SERVER_ERROR status`() {
        val exception = Exception("Unexpected error")

        val result = handler.handleGeneric(exception)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        assertEquals("INTERNAL_SERVER_ERROR", result.body?.code)
        assertEquals("Unexpected server error", result.body?.message)
    }

    @Test
    fun `should handle RuntimeException with generic handler`() {
        val exception = RuntimeException("Some runtime error")

        val result = handler.handleGeneric(exception)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        assertEquals("INTERNAL_SERVER_ERROR", result.body?.code)
        assertEquals("Unexpected server error", result.body?.message)
    }

    @Test
    fun `should handle NullPointerException with generic handler`() {
        val exception = NullPointerException("Null pointer")

        val result = handler.handleGeneric(exception)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
        assertEquals("INTERNAL_SERVER_ERROR", result.body?.code)
        assertEquals("Unexpected server error", result.body?.message)
    }
}
