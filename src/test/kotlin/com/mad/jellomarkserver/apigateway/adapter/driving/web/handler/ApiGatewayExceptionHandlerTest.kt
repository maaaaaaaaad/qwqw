package com.mad.jellomarkserver.apigateway.adapter.driving.web.handler

import com.mad.jellomarkserver.auth.core.domain.exception.AuthenticationFailedException
import com.mad.jellomarkserver.auth.core.domain.exception.DuplicateAuthEmailException
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidAuthEmailException
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidRawPasswordException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateSocialAccountException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.exception.DuplicateShopRegNumException
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationTimeConflictException
import com.mad.jellomarkserver.review.core.domain.exception.DuplicateReviewException
import com.mad.jellomarkserver.treatment.core.domain.exception.InvalidTreatmentNameException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ApiGatewayExceptionHandlerTest {

    private val handler = ApiGatewayExceptionHandler()

    @Test
    fun `should handle DuplicateSocialAccountException with CONFLICT status`() {
        val exception = DuplicateSocialAccountException("KAKAO", "123456789")

        val result = handler.handleDuplicateSocialAccount(exception)

        assertEquals(HttpStatus.CONFLICT.value(), result.status)
        assertEquals("Social account already exists: KAKAO:123456789", result.detail)
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

        val result = handler.handleInvalidMemberNickname(exception)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.status)
        assertEquals("Invalid nickname: a", result.detail)
    }

    @Test
    fun `should handle MemberNotFoundException with NOT_FOUND status`() {
        val exception = MemberNotFoundException("KAKAO:123456789")

        val result = handler.handleMemberNotFound(exception)

        assertEquals(HttpStatus.NOT_FOUND.value(), result.status)
        assertEquals("Member not found: KAKAO:123456789", result.detail)
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
    fun `should handle AuthenticationFailedException with UNAUTHORIZED status`() {
        val exception = AuthenticationFailedException("test@example.com")

        val result = handler.handleAuthenticationFailed(exception)

        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.status)
        assertEquals("Authentication failed for email test@example.com", result.detail)
    }

    @Test
    fun `should handle DuplicateAuthEmailException with CONFLICT status`() {
        val exception = DuplicateAuthEmailException("auth@example.com")

        val result = handler.handleDuplicateAuthEmail(exception)

        assertEquals(HttpStatus.CONFLICT.value(), result.status)
        assertEquals("Email already in use: auth@example.com", result.detail)
    }

    @Test
    fun `should handle InvalidAuthEmailException with UNPROCESSABLE_ENTITY status`() {
        val exception = InvalidAuthEmailException("invalid-auth-email")

        val result = handler.handleInvalidAuthEmail(exception)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.status)
        assertEquals("Invalid email invalid-auth-email", result.detail)
    }

    @Test
    fun `should handle InvalidRawPasswordException with UNPROCESSABLE_ENTITY status`() {
        val exception = InvalidRawPasswordException("Password too short")

        val result = handler.handleInvalidPassword(exception)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.status)
        assertEquals("Password too short", result.detail)
    }

    @Test
    fun `should include error code in ProblemDetail response`() {
        val exception = DuplicateAuthEmailException("test@test.com")

        val result = handler.handleDuplicateAuthEmail(exception)

        assertEquals("DUPLICATE_AUTH_EMAIL", result.properties?.get("code"))
    }

    @Test
    fun `should derive error code from exception class name`() {
        val cases = mapOf(
            handler.handleAuthenticationFailed(AuthenticationFailedException("e")) to "AUTHENTICATION_FAILED",
            handler.handleInvalidMemberNickname(InvalidMemberNicknameException("n")) to "INVALID_MEMBER_NICKNAME",
            handler.handleMemberNotFound(MemberNotFoundException("m")) to "MEMBER_NOT_FOUND",
            handler.handleDuplicateShopRegNum(DuplicateShopRegNumException("r")) to "DUPLICATE_SHOP_REG_NUM",
            handler.handleReservationTimeConflict(ReservationTimeConflictException("s", "d", "09:00", "10:00")) to "RESERVATION_TIME_CONFLICT",
            handler.handleDuplicateReview(DuplicateReviewException("r")) to "DUPLICATE_REVIEW",
            handler.handleInvalidTreatmentName(InvalidTreatmentNameException("t")) to "INVALID_TREATMENT_NAME",
        )

        cases.forEach { (result, expectedCode) ->
            assertEquals(expectedCode, result.properties?.get("code"), "Expected code $expectedCode")
        }
    }
}
