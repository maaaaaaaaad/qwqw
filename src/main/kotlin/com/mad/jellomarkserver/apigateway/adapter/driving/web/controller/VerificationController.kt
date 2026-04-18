package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SendVerificationRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.VerifyCodeRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.VerificationResponse
import com.mad.jellomarkserver.owner.core.domain.exception.DuplicateOwnerEmailException
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.verification.core.domain.exception.InvalidVerificationCodeException
import com.mad.jellomarkserver.verification.core.domain.model.VerificationToken
import com.mad.jellomarkserver.verification.port.driven.SmsVerificationPort
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
    private val ownerPort: OwnerPort,
    private val smsVerificationPort: SmsVerificationPort
) {

    @PostMapping("/api/verification/send")
    @ResponseStatus(HttpStatus.OK)
    fun sendVerificationCode(@RequestBody request: SendVerificationRequest) {
        val target = request.target.trim()

        when (request.type.uppercase()) {
            "EMAIL" -> {
                val email = target.lowercase()
                val existingOwner = ownerPort.findByEmail(OwnerEmail.of(email))

                when (request.purpose?.uppercase()) {
                    "RESET_PASSWORD" -> {
                        if (existingOwner == null) throw OwnerNotFoundException(email)
                    }
                    else -> {
                        if (existingOwner != null) throw DuplicateOwnerEmailException(email)
                    }
                }

                sendVerificationCodeUseCase.execute(
                    SendVerificationCodeCommand(target = email, type = request.type)
                )
            }
            "SMS" -> {
                smsVerificationPort.sendCode(target)
            }
        }
    }

    @PostMapping("/api/verification/verify")
    @ResponseStatus(HttpStatus.OK)
    fun verifyCode(@RequestBody request: VerifyCodeRequest): VerificationResponse {
        when (request.type.uppercase()) {
            "SMS" -> {
                val approved = smsVerificationPort.checkCode(request.target.trim(), request.code)
                if (!approved) throw InvalidVerificationCodeException()
                val token = VerificationToken.generate()
                return VerificationResponse(verified = true, verificationToken = token.value)
            }
            else -> {
                val token = verifyCodeUseCase.execute(
                    VerifyCodeCommand(target = request.target, code = request.code, type = request.type)
                )
                return VerificationResponse(verified = true, verificationToken = token.value)
            }
        }
    }
}
