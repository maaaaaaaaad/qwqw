package com.mad.jellomarkserver.owner.adapter.driving.web

import com.mad.jellomarkserver.owner.adapter.driving.web.request.OwnerSignUpRequest
import com.mad.jellomarkserver.owner.adapter.driving.web.response.OwnerResponse
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerCommand
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

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
            nickname = request.nickname,
            email = request.email,
        )
        val owner = signUpOwnerUseCase.signUp(command)
        return OwnerResponse.from(owner)
    }
}
