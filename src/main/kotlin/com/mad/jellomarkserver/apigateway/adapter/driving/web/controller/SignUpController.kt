package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.SignUpResponse
import com.mad.jellomarkserver.apigateway.port.driving.SignUpOwnerOrchestrator
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class SignUpController(
    private val signUpOwnerOrchestrator: SignUpOwnerOrchestrator
) {

    @PostMapping("/api/sign-up/owner")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUpOwner(@RequestBody request: SignUpOwnerRequest): SignUpResponse {
        return signUpOwnerOrchestrator.signUp(request)
    }
}
