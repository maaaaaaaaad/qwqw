package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateDesignerRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.UpdateDesignerRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.DesignerResponse
import com.mad.jellomarkserver.designer.port.driving.*
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class DesignerController(
    private val createDesignerUseCase: CreateDesignerUseCase,
    private val getDesignerUseCase: GetDesignerUseCase,
    private val listDesignersUseCase: ListDesignersUseCase,
    private val updateDesignerUseCase: UpdateDesignerUseCase,
    private val deleteDesignerUseCase: DeleteDesignerUseCase,
    private val ownerPort: OwnerPort
) {
    @PostMapping("/api/beautishops/{shopId}/designers")
    @ResponseStatus(HttpStatus.CREATED)
    fun createDesigner(
        @PathVariable shopId: String,
        @RequestBody request: CreateDesignerRequest,
        servletRequest: HttpServletRequest
    ): DesignerResponse {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can create designers")
        }

        val owner = ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = CreateDesignerCommand(
            shopId = shopId,
            ownerId = owner.id.value.toString(),
            name = request.name,
            nickname = request.nickname,
            intro = request.intro,
            photoUrls = request.photoUrls
        )

        val designer = createDesignerUseCase.create(command)
        return DesignerResponse.from(designer)
    }

    @GetMapping("/api/beautishops/{shopId}/designers")
    fun listDesigners(@PathVariable shopId: String): List<DesignerResponse> {
        val command = ListDesignersCommand(shopId = shopId)
        val designers = listDesignersUseCase.execute(command)
        return designers.map { DesignerResponse.from(it) }
    }

    @GetMapping("/api/beautishops/{shopId}/designers/{designerId}")
    fun getDesigner(
        @PathVariable shopId: String,
        @PathVariable designerId: String
    ): DesignerResponse {
        val command = GetDesignerCommand(designerId = designerId)
        val designer = getDesignerUseCase.execute(command)
        return DesignerResponse.from(designer)
    }

    @PatchMapping("/api/beautishops/{shopId}/designers/{designerId}")
    fun updateDesigner(
        @PathVariable shopId: String,
        @PathVariable designerId: String,
        @RequestBody request: UpdateDesignerRequest,
        servletRequest: HttpServletRequest
    ): DesignerResponse {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can update designers")
        }

        val owner = ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = UpdateDesignerCommand(
            designerId = designerId,
            ownerId = owner.id.value.toString(),
            name = request.name,
            nickname = request.nickname,
            intro = request.intro,
            photoUrls = request.photoUrls
        )

        val designer = updateDesignerUseCase.update(command)
        return DesignerResponse.from(designer)
    }

    @DeleteMapping("/api/beautishops/{shopId}/designers/{designerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDesigner(
        @PathVariable shopId: String,
        @PathVariable designerId: String,
        servletRequest: HttpServletRequest
    ) {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can delete designers")
        }

        val owner = ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = DeleteDesignerCommand(
            designerId = designerId,
            ownerId = owner.id.value.toString()
        )

        deleteDesignerUseCase.delete(command)
    }
}
