package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.member.adapter.driving.web.response.MemberResponse
import com.mad.jellomarkserver.member.port.driving.GetCurrentMemberCommand
import com.mad.jellomarkserver.member.port.driving.GetCurrentMemberUseCase
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController(
    private val getCurrentMemberUseCase: GetCurrentMemberUseCase
) {
    @GetMapping("/api/members/me")
    @ResponseStatus(HttpStatus.OK)
    fun getCurrentMember(request: HttpServletRequest): MemberResponse {
        val email = request.getAttribute("email") as String
        val command = GetCurrentMemberCommand(email = email)
        val member = getCurrentMemberUseCase.execute(command)
        return MemberResponse.from(member)
    }
}
