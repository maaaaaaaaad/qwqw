package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SendVerificationRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.VerifyCodeRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.VerificationResponse
import com.mad.jellomarkserver.owner.core.domain.exception.DuplicateOwnerEmailException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.verification.port.driving.SendVerificationCodeCommand
import com.mad.jellomarkserver.verification.port.driving.SendVerificationCodeUseCase
import com.mad.jellomarkserver.verification.port.driving.VerifyCodeCommand
import com.mad.jellomarkserver.verification.port.driving.VerifyCodeUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class VerificationController(
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val ownerPort: OwnerPort
) {

    @PostMapping("/api/verification/send")
    @ResponseStatus(HttpStatus.OK)
    fun sendVerificationCode(@RequestBody request: SendVerificationRequest) {
        val target = request.target.trim().lowercase()

        if (request.type.uppercase() == "EMAIL") {
            ownerPort.findByEmail(OwnerEmail.of(target))?.let {
                throw DuplicateOwnerEmailException(target)
            }
        }

        sendVerificationCodeUseCase.execute(
            SendVerificationCodeCommand(target = target, type = request.type)
        )
    }

    @PostMapping("/api/verification/verify")
    @ResponseStatus(HttpStatus.OK)
    fun verifyCode(@RequestBody request: VerifyCodeRequest): VerificationResponse {
        val token = verifyCodeUseCase.execute(
            VerifyCodeCommand(target = request.target, code = request.code, type = request.type)
        )
        return VerificationResponse(verified = true, verificationToken = token.value)
    }
}
