package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SetShopCategoriesRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.UpdateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.BeautishopResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.CategoryResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.PagedBeautishopsResponse
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driving.*
import com.mad.jellomarkserver.category.port.driven.ShopCategoryPort
import com.mad.jellomarkserver.category.port.driving.SetShopCategoriesCommand
import com.mad.jellomarkserver.category.port.driving.SetShopCategoriesUseCase
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class BeautishopController(
    private val createBeautishopUseCase: CreateBeautishopUseCase,
    private val updateBeautishopUseCase: UpdateBeautishopUseCase,
    private val deleteBeautishopUseCase: DeleteBeautishopUseCase,
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
            shopImages = request.shopImages
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

    @PutMapping("/api/beautishops/{shopId}")
    fun updateBeautishop(
        @PathVariable shopId: String,
        @RequestBody request: UpdateBeautishopRequest,
        servletRequest: HttpServletRequest
    ): BeautishopResponse {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can update beautishops")
        }

        val owner = ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = UpdateBeautishopCommand(
            shopId = shopId,
            ownerId = owner.id.value.toString(),
            operatingTime = request.operatingTime,
            shopDescription = request.shopDescription,
            shopImages = request.shopImages
        )

        val beautishop = updateBeautishopUseCase.update(command)
        val categories = shopCategoryPort.findCategoriesByShopId(
            ShopId.from(UUID.fromString(shopId))
        )
        return BeautishopResponse.from(beautishop, categories)
    }

    @DeleteMapping("/api/beautishops/{shopId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBeautishop(
        @PathVariable shopId: String,
        servletRequest: HttpServletRequest
    ) {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can delete beautishops")
        }

        val owner = ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = DeleteBeautishopCommand(
            shopId = shopId,
            ownerId = owner.id.value.toString()
        )

        deleteBeautishopUseCase.delete(command)
    }

    @GetMapping("/api/beautishops")
    fun listBeautishops(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) categoryId: UUID?,
        @RequestParam(required = false) minRating: Double?,
        @RequestParam(defaultValue = "CREATED_AT") sortBy: SortBy,
        @RequestParam(defaultValue = "DESC") sortOrder: SortOrder,
        @RequestParam(required = false) latitude: Double?,
        @RequestParam(required = false) longitude: Double?,
        @RequestParam(required = false) radiusKm: Double?
    ): PagedBeautishopsResponse {
        val command = ListBeautishopsCommand(
            page = page,
            size = size,
            keyword = keyword,
            categoryId = categoryId,
            minRating = minRating,
            sortBy = sortBy,
            sortOrder = sortOrder,
            latitude = latitude,
            longitude = longitude,
            radiusKm = radiusKm
        )
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
