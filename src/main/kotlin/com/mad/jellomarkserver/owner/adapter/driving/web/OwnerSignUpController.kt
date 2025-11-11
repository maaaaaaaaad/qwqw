package com.mad.jellomarkserver.owner.adapter.driving.web

import com.mad.jellomarkserver.owner.adapter.driving.web.request.OwnerSignUpRequest
import com.mad.jellomarkserver.owner.adapter.driving.web.response.OwnerResponse
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerCommand
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/owners")
class OwnerSignUpController(
    private val signUpOwnerUseCase: SignUpOwnerUseCase
) {
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(@RequestBody request: OwnerSignUpRequest): OwnerResponse {
        val command = SignUpOwnerCommand(
            businessNumber = request.businessNumber,
            phoneNumber = request.phoneNumber,
        )
        val owner = signUpOwnerUseCase.signUp(command)
        return OwnerResponse.from(owner)
    }
}
