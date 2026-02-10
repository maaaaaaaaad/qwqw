package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.RegisterDeviceTokenRequest
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.notification.port.driving.RegisterDeviceTokenCommand
import com.mad.jellomarkserver.notification.port.driving.RegisterDeviceTokenUseCase
import com.mad.jellomarkserver.notification.port.driving.UnregisterDeviceTokenCommand
import com.mad.jellomarkserver.notification.port.driving.UnregisterDeviceTokenUseCase
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class DeviceTokenController(
    private val registerDeviceTokenUseCase: RegisterDeviceTokenUseCase,
    private val unregisterDeviceTokenUseCase: UnregisterDeviceTokenUseCase,
    private val memberPort: MemberPort,
    private val ownerPort: OwnerPort
) {

    @PostMapping("/api/device-tokens")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerDeviceToken(
        @RequestBody request: RegisterDeviceTokenRequest,
        servletRequest: HttpServletRequest
    ) {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        val userId = resolveUserId(identifier, userType)

        registerDeviceTokenUseCase.execute(
            RegisterDeviceTokenCommand(
                userId = userId,
                userRole = userType,
                token = request.token,
                platform = request.platform
            )
        )
    }

    @DeleteMapping("/api/device-tokens/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unregisterDeviceToken(@PathVariable token: String) {
        unregisterDeviceTokenUseCase.execute(UnregisterDeviceTokenCommand(token))
    }

    private fun resolveUserId(identifier: String, userType: String): String {
        return when (userType) {
            "MEMBER" -> {
                val member = memberPort.findBySocialId(SocialId(identifier))
                    ?: throw MemberNotFoundException(identifier)
                member.id.value.toString()
            }
            "OWNER" -> {
                val owner = ownerPort.findByEmail(OwnerEmail.of(identifier))
                    ?: throw OwnerNotFoundException(identifier)
                owner.id.value.toString()
            }
            else -> throw IllegalStateException("Unknown user type: $userType")
        }
    }
}
