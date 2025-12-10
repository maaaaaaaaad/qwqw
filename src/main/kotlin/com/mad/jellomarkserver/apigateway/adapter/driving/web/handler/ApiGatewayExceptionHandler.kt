package com.mad.jellomarkserver.apigateway.adapter.driving.web.handler

import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.ErrorResponse
import com.mad.jellomarkserver.auth.core.domain.exception.AuthenticationFailedException
import com.mad.jellomarkserver.auth.core.domain.exception.DuplicateAuthEmailException
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidAuthEmailException
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidRawPasswordException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
import com.mad.jellomarkserver.owner.core.domain.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.mad.jellomarkserver.apigateway"])
class ApiGatewayExceptionHandler {

    @ExceptionHandler(AuthenticationFailedException::class)
    fun handleAuthenticationFailed(ex: AuthenticationFailedException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.message)
    }

    @ExceptionHandler(InvalidAuthEmailException::class)
    fun handleInvalidAuthEmail(ex: InvalidAuthEmailException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(DuplicateAuthEmailException::class)
    fun handleDuplicateAuthEmail(ex: DuplicateAuthEmailException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidRawPasswordException::class)
    fun handleInvalidPassword(ex: InvalidRawPasswordException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

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

    @ExceptionHandler(DuplicateOwnerNicknameException::class)
    fun handleDuplicateOwnerNickname(ex: DuplicateOwnerNicknameException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidOwnerNicknameException::class)
    fun handleInvalidOwnerNickname(ex: InvalidOwnerNicknameException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(DuplicateOwnerBusinessNumberException::class)
    fun handleDuplicateOwnerBusinessNumber(ex: DuplicateOwnerBusinessNumberException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidOwnerBusinessNumberException::class)
    fun handleInvalidOwnerBusinessNumber(ex: InvalidOwnerBusinessNumberException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(DuplicateOwnerPhoneNumberException::class)
    fun handleDuplicateOwnerPhoneNumber(ex: DuplicateOwnerPhoneNumberException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidOwnerPhoneNumberException::class)
    fun handleInvalidOwnerPhoneNumber(ex: InvalidOwnerPhoneNumberException): ProblemDetail {
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
