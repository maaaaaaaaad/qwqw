package com.mad.jellomarkserver.apigateway.core.orchestration

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.SignUpResponse
import com.mad.jellomarkserver.apigateway.port.driving.SignUpOwnerOrchestrator
import com.mad.jellomarkserver.auth.port.driving.SignUpAuthCommand
import com.mad.jellomarkserver.auth.port.driving.SignUpAuthUseCase
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerCommand
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpOwnerOrchestratorImpl(
    private val signUpOwnerUseCase: SignUpOwnerUseCase,
    private val signUpAuthUseCase: SignUpAuthUseCase
) : SignUpOwnerOrchestrator {

    @Transactional
    override fun signUp(request: SignUpOwnerRequest): SignUpResponse {
        val ownerCommand = SignUpOwnerCommand(
            businessNumber = request.businessNumber,
            phoneNumber = request.phoneNumber,
            nickname = request.nickname
        )

        val owner = signUpOwnerUseCase.signUp(ownerCommand)

        val authCommand = SignUpAuthCommand(
            email = request.email,
            password = request.password,
            userType = "OWNER"
        )

        signUpAuthUseCase.signUp(authCommand)

        return SignUpResponse.fromOwner(owner)
    }
}
