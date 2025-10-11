package com.mad.jellomarkserver.member.adapter.driving.web.handler

import com.mad.jellomarkserver.member.adapter.driving.web.response.ErrorResponse
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidEmailException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidNicknameException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class MemberRestExceptionHandler {
    @ExceptionHandler(DuplicateEmailException::class)
    fun handleDuplicateEmail(ex: DuplicateEmailException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
        return problemDetail
    }

    @ExceptionHandler(DuplicateNicknameException::class)
    fun handleDuplicateMemberNickname(ex: DuplicateNicknameException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "MEMBER_DUPLICATE_NICKNAME",
            message = ex.message ?: "Duplicate nickname"
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body)
    }

    @ExceptionHandler(InvalidEmailException::class)
    fun handleInvalidNickname(ex: InvalidEmailException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "MEMBER_EMAIL_INVALID",
            message = ex.message ?: "Invalid Member Email"
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body)
    }

    @ExceptionHandler(InvalidNicknameException::class)
    fun handleInvalidNickname(ex: InvalidNicknameException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "MEMBER_NICKNAME_INVALID",
            message = ex.message ?: "Invalid Member Nickname"
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "INTERNAL_SERVER_ERROR",
            message = "Unexpected server error"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }
}