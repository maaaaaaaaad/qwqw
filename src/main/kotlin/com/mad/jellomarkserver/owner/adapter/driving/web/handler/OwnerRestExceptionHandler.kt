package com.mad.jellomarkserver.owner.adapter.driving.web.handler

import com.mad.jellomarkserver.member.adapter.driving.web.response.ErrorResponse
import com.mad.jellomarkserver.owner.core.domain.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.mad.jellomarkserver.owner"])
class OwnerRestExceptionHandler {
    @ExceptionHandler(InvalidOwnerBusinessNumberException::class)
    fun handleInvalidBusinessNumber(ex: InvalidOwnerBusinessNumberException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
        return problemDetail
    }

    @ExceptionHandler(InvalidOwnerPhoneNumberException::class)
    fun handleInvalidPhoneNumber(ex: InvalidOwnerPhoneNumberException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
        return problemDetail
    }

    @ExceptionHandler(DuplicateOwnerPhoneNumberException::class)
    fun handleDuplicatePhoneNumber(ex: DuplicateOwnerPhoneNumberException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
        return problemDetail
    }

    @ExceptionHandler(DuplicateOwnerBusinessNumberException::class)
    fun handleDuplicateBusinessNumber(ex: DuplicateOwnerBusinessNumberException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
        return problemDetail
    }

    @ExceptionHandler(InvalidOwnerNicknameException::class)
    fun handleInvalidNickname(ex: InvalidOwnerNicknameException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
        return problemDetail
    }

    @ExceptionHandler(DuplicateOwnerNicknameException::class)
    fun handleDuplicateNickname(ex: DuplicateOwnerNicknameException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
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