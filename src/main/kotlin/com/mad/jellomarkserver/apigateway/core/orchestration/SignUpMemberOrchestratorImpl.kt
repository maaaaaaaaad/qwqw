package com.mad.jellomarkserver.apigateway.core.orchestration

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpMemberRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.SignUpResponse
import com.mad.jellomarkserver.apigateway.port.driving.SignUpMemberOrchestrator
import com.mad.jellomarkserver.auth.port.driving.SignUpAuthCommand
import com.mad.jellomarkserver.auth.port.driving.SignUpAuthUseCase
import com.mad.jellomarkserver.member.port.driving.SignUpMemberCommand
import com.mad.jellomarkserver.member.port.driving.SignUpMemberUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpMemberOrchestratorImpl(
    private val signUpMemberUseCase: SignUpMemberUseCase,
    private val signUpAuthUseCase: SignUpAuthUseCase
) : SignUpMemberOrchestrator {

    @Transactional
    override fun signUp(request: SignUpMemberRequest): SignUpResponse {
        val memberCommand = SignUpMemberCommand(
            nickname = request.nickname,
            email = request.email
        )

        val member = signUpMemberUseCase.signUp(memberCommand)

        val authCommand = SignUpAuthCommand(
            email = request.email,
            password = request.password,
            userType = "MEMBER"
        )

        signUpAuthUseCase.signUp(authCommand)

        return SignUpResponse.fromMember(member)
    }
}
