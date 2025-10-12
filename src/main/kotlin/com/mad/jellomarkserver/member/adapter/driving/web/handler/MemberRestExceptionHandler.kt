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

    @ExceptionHandler(InvalidEmailException::class)
    fun handleInvalidEmail(ex: InvalidEmailException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
        return problemDetail
    }

    @ExceptionHandler(DuplicateNicknameException::class)
    fun handleDuplicateMemberNickname(ex: DuplicateNicknameException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
        return problemDetail
    }

    @ExceptionHandler(InvalidNicknameException::class)
    fun handleInvalidNickname(ex: InvalidNicknameException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
        return problemDetail
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