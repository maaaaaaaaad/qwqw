package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.BeautishopResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.OwnerResponse
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driving.GetOwnerBeautishopsCommand
import com.mad.jellomarkserver.beautishop.port.driving.GetOwnerBeautishopsUseCase
import com.mad.jellomarkserver.category.port.driven.ShopCategoryPort
import com.mad.jellomarkserver.owner.port.driving.GetCurrentOwnerCommand
import com.mad.jellomarkserver.owner.port.driving.GetCurrentOwnerUseCase
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class OwnerController(
    private val getCurrentOwnerUseCase: GetCurrentOwnerUseCase,
    private val getOwnerBeautishopsUseCase: GetOwnerBeautishopsUseCase,
    private val shopCategoryPort: ShopCategoryPort
) {
    @GetMapping("/api/owners/me")
    @ResponseStatus(HttpStatus.OK)
    fun getCurrentOwner(request: HttpServletRequest): OwnerResponse {
        val email = request.getAttribute("email") as String
        val command = GetCurrentOwnerCommand(email = email)
        val owner = getCurrentOwnerUseCase.execute(command)
        return OwnerResponse.from(owner)
    }

    @GetMapping("/api/owners/me/beautishops")
    @ResponseStatus(HttpStatus.OK)
    fun getOwnerBeautishops(request: HttpServletRequest): List<BeautishopResponse> {
        val email = request.getAttribute("email") as String
        val command = GetOwnerBeautishopsCommand(email = email)
        val beautishops = getOwnerBeautishopsUseCase.execute(command)
        return beautishops.map { shop ->
            val categories = shopCategoryPort.findCategoriesByShopId(shop.id)
            BeautishopResponse.from(shop, categories)
        }
    }
}
