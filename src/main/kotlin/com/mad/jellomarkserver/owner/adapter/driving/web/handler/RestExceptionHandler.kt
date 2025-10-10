package com.mad.jellomarkserver.owner.adapter.driving.web.handler

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidBusinessNumberException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(InvalidBusinessNumberException::class)
    fun handleInvalidBusinessNumber(ex: InvalidBusinessNumberException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
        return problemDetail
    }
}