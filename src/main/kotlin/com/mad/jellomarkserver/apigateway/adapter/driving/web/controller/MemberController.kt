package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.WithdrawMemberRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.MemberResponse
import com.mad.jellomarkserver.member.port.driving.GetCurrentMemberCommand
import com.mad.jellomarkserver.member.port.driving.GetCurrentMemberUseCase
import com.mad.jellomarkserver.member.port.driving.WithdrawMemberCommand
import com.mad.jellomarkserver.member.port.driving.WithdrawMemberUseCase
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController(
    private val getCurrentMemberUseCase: GetCurrentMemberUseCase,
    private val withdrawMemberUseCase: WithdrawMemberUseCase
) {
    @GetMapping("/api/members/me")
    @ResponseStatus(HttpStatus.OK)
    fun getCurrentMember(request: HttpServletRequest): MemberResponse {
        val socialProvider = request.getAttribute("socialProvider") as String
        val socialId = request.getAttribute("socialId") as String
        val command = GetCurrentMemberCommand(socialProvider = socialProvider, socialId = socialId)
        val member = getCurrentMemberUseCase.execute(command)
        return MemberResponse.from(member)
    }

    @PostMapping("/api/members/me/withdraw")
    @ResponseStatus(HttpStatus.OK)
    fun withdraw(
        @RequestBody body: WithdrawMemberRequest,
        request: HttpServletRequest
    ) {
        val socialProvider = request.getAttribute("socialProvider") as String
        val socialId = request.getAttribute("socialId") as String
        withdrawMemberUseCase.withdraw(
            WithdrawMemberCommand(
                socialProvider = socialProvider,
                socialId = socialId,
                reason = body.reason
            )
        )
    }
}
