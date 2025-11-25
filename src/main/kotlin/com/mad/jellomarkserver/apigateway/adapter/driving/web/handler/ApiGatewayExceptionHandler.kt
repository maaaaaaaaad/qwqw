package com.mad.jellomarkserver.apigateway.adapter.driving.web.handler

import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.ErrorResponse
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.mad.jellomarkserver.apigateway"])
class ApiGatewayExceptionHandler {

    @ExceptionHandler(DuplicateMemberEmailException::class)
    fun handleDuplicateMemberEmail(ex: DuplicateMemberEmailException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidMemberEmailException::class)
    fun handleInvalidMemberEmail(ex: InvalidMemberEmailException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(DuplicateMemberNicknameException::class)
    fun handleDuplicateMemberNickname(ex: DuplicateMemberNicknameException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidMemberNicknameException::class)
    fun handleInvalidMemberNickname(ex: InvalidMemberNicknameException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
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
