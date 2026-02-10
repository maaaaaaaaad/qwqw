package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.UsageHistoryResponse
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.usagehistory.port.driving.ListMemberUsageHistoryCommand
import com.mad.jellomarkserver.usagehistory.port.driving.ListMemberUsageHistoryUseCase
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UsageHistoryController(
    private val listMemberUsageHistoryUseCase: ListMemberUsageHistoryUseCase,
    private val memberPort: MemberPort
) {

    @GetMapping("/api/usage-history/me")
    fun getMyUsageHistory(servletRequest: HttpServletRequest): List<UsageHistoryResponse> {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can view usage history")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = ListMemberUsageHistoryCommand(member.id.value.toString())
        val usageHistories = listMemberUsageHistoryUseCase.execute(command)
        return usageHistories.map { UsageHistoryResponse.from(it) }
    }
}
