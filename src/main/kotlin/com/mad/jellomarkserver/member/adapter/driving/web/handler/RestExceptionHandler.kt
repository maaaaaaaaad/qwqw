package com.mad.jellomarkserver.member.adapter.driving.web.handler

import com.mad.jellomarkserver.member.adapter.driving.web.response.ErrorResponse
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidBusinessNumberException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateNicknameException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class RestExceptionHandler {
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

    @ExceptionHandler(InvalidBusinessNumberException::class)
    fun handleInvalidBusinessNumber(ex: InvalidBusinessNumberException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "BUSINESS_NUMBER_INVALID",
            message = ex.message ?: "Invalid Business Number"
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "INVALID_ARGUMENT",
            message = ex.message ?: "Invalid request parameter"
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "INVALID_JSON",
            message = ex.mostSpecificCause.message ?: "Malformed JSON request"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "TYPE_MISMATCH",
            message = "Parameter '${ex.name}' type mismatch"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleBeanValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val firstError = ex.bindingResult.fieldErrors.firstOrNull()
        val message = firstError?.let { "${it.field}: ${it.defaultMessage}" } ?: "Validation failed"
        val body = ErrorResponse(
            code = "VALIDATION_ERROR",
            message = message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
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