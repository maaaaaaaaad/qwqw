package com.mad.jellomarkserver.member.adapter.`in`.web.handler

import com.mad.jellomarkserver.member.adapter.`in`.web.response.ErrorResponse
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateEmailException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(DuplicateEmailException::class)
    fun handleDuplicateEmail(ex: DuplicateEmailException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "MEMBER_DUPLICATE_EMAIL",
            message = ex.message ?: "Duplicate email"
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body)
    }
}