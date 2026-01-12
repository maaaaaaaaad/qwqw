package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SetShopCategoriesRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.CategoryResponse
import com.mad.jellomarkserver.beautishop.adapter.driving.web.response.BeautishopResponse
import com.mad.jellomarkserver.beautishop.adapter.driving.web.response.PagedBeautishopsResponse
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driving.CreateBeautishopCommand
import com.mad.jellomarkserver.beautishop.port.driving.CreateBeautishopUseCase
import com.mad.jellomarkserver.beautishop.port.driving.GetBeautishopCommand
import com.mad.jellomarkserver.beautishop.port.driving.GetBeautishopUseCase
import com.mad.jellomarkserver.beautishop.port.driving.ListBeautishopsCommand
import com.mad.jellomarkserver.beautishop.port.driving.ListBeautishopsUseCase
import com.mad.jellomarkserver.category.port.driven.ShopCategoryPort
import com.mad.jellomarkserver.category.port.driving.SetShopCategoriesCommand
import com.mad.jellomarkserver.category.port.driving.SetShopCategoriesUseCase
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class BeautishopController(
    private val createBeautishopUseCase: CreateBeautishopUseCase,
    private val getBeautishopUseCase: GetBeautishopUseCase,
    private val listBeautishopsUseCase: ListBeautishopsUseCase,
    private val ownerPort: OwnerPort,
    private val shopCategoryPort: ShopCategoryPort,
    private val setShopCategoriesUseCase: SetShopCategoriesUseCase
) {
    @PostMapping("/api/beautishops")
    @ResponseStatus(HttpStatus.CREATED)
    fun createBeautishop(
        @RequestBody request: CreateBeautishopRequest,
        servletRequest: HttpServletRequest
    ): BeautishopResponse {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can create beautishops")
        }

        val owner = ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = CreateBeautishopCommand(
            ownerId = owner.id.value.toString(),
            shopName = request.shopName,
            shopRegNum = request.shopRegNum,
            shopPhoneNumber = request.shopPhoneNumber,
            shopAddress = request.shopAddress,
            latitude = request.latitude,
            longitude = request.longitude,
            operatingTime = request.operatingTime,
            shopDescription = request.shopDescription,
            shopImage = request.shopImage
        )

        val beautishop = createBeautishopUseCase.create(command)
        return BeautishopResponse.from(beautishop)
    }

    @GetMapping("/api/beautishops/{shopId}")
    fun getBeautishop(@PathVariable shopId: String): BeautishopResponse {
        val command = GetBeautishopCommand(shopId = shopId)
        val beautishop = getBeautishopUseCase.execute(command)
        val categories = shopCategoryPort.findCategoriesByShopId(
            ShopId.from(UUID.fromString(shopId))
        )
        return BeautishopResponse.from(beautishop, categories)
    }

    @GetMapping("/api/beautishops")
    fun listBeautishops(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): PagedBeautishopsResponse {
        val command = ListBeautishopsCommand(page = page, size = size)
        val result = listBeautishopsUseCase.execute(command)
        return PagedBeautishopsResponse.from(result)
    }

    @PutMapping("/api/beautishops/{shopId}/categories")
    fun setShopCategories(
        @PathVariable shopId: String,
        @RequestBody request: SetShopCategoriesRequest,
        servletRequest: HttpServletRequest
    ): List<CategoryResponse> {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can set shop categories")
        }

        val owner = ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = SetShopCategoriesCommand(
            shopId = shopId,
            ownerId = owner.id.value.toString(),
            categoryIds = request.categoryIds
        )

        val categories = setShopCategoriesUseCase.execute(command)
        return categories.map { CategoryResponse.from(it) }
    }
}
