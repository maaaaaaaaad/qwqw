package com.mad.jellomarkserver.member.adapter.`in`.web

import com.mad.jellomarkserver.member.adapter.`in`.web.response.MemberResponse
import com.mad.jellomarkserver.member.adapter.`in`.web.request.MemberSignUpRequest
import com.mad.jellomarkserver.member.port.driving.SignUpMemberCommand
import com.mad.jellomarkserver.member.port.driving.SignUpMemberUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberSignUpController(
    private val signUpMemberUseCase: SignUpMemberUseCase
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(@RequestBody request: MemberSignUpRequest): MemberResponse {
        val command = SignUpMemberCommand(
            nickname = request.nickname,
            email = request.email,
            businessRegistrationNumber = request.businessRegistrationNumber
        )
        val member = signUpMemberUseCase.signUp(command)
        return MemberResponse.from(member)
    }
}
