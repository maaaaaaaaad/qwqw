package com.mad.jellomarkserver.owner.adapter.driving.web.handler

import com.mad.jellomarkserver.owner.core.domain.exception.DuplicateOwnerPhoneNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerBusinessNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerPhoneNumberException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
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
}