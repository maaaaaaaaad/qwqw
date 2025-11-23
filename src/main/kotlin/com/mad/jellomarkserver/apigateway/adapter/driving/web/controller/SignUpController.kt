package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpMemberRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.SignUpResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class SignUpController {

    @PostMapping("/api/sign-up/member")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUpMember(@RequestBody request: SignUpMemberRequest): SignUpResponse {
        TODO("Not yet implemented")
    }

    @PostMapping("/api/sign-up/owner")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUpOwner(@RequestBody request: SignUpOwnerRequest): SignUpResponse {
        TODO("Not yet implemented")
    }
}
