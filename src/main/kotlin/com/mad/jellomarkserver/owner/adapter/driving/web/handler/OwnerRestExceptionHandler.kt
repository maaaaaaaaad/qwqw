package com.mad.jellomarkserver.owner.adapter.driving.web.handler

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidBusinessNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidPhoneNumberException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class OwnerRestExceptionHandler {
    @ExceptionHandler(InvalidBusinessNumberException::class)
    fun handleInvalidBusinessNumber(ex: InvalidBusinessNumberException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
        return problemDetail
    }

    @ExceptionHandler(InvalidPhoneNumberException::class)
    fun handleInvalidPhoneNumber(ex: InvalidPhoneNumberException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
        return problemDetail
    }
}