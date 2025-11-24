package com.mad.jellomarkserver.apigateway.adapter.driving.web.handler

import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.mad.jellomarkserver.apigateway"])
class ApiGatewayExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "INTERNAL_SERVER_ERROR",
            message = "Unexpected server error"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }
}
