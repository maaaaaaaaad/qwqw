package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.owner.adapter.driving.web.response.OwnerResponse
import com.mad.jellomarkserver.owner.port.driving.GetCurrentOwnerCommand
import com.mad.jellomarkserver.owner.port.driving.GetCurrentOwnerUseCase
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class OwnerController(
    private val getCurrentOwnerUseCase: GetCurrentOwnerUseCase
) {
    @GetMapping("/api/owners/me")
    @ResponseStatus(HttpStatus.OK)
    fun getCurrentOwner(request: HttpServletRequest): OwnerResponse {
        val email = request.getAttribute("email") as String
        val command = GetCurrentOwnerCommand(email = email)
        val owner = getCurrentOwnerUseCase.execute(command)
        return OwnerResponse.from(owner)
    }
}
