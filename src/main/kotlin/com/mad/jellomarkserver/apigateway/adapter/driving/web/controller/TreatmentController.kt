package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateTreatmentRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.UpdateTreatmentRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.TreatmentResponse
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.treatment.port.driving.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class TreatmentController(
    private val createTreatmentUseCase: CreateTreatmentUseCase,
    private val getTreatmentUseCase: GetTreatmentUseCase,
    private val listTreatmentsUseCase: ListTreatmentsUseCase,
    private val updateTreatmentUseCase: UpdateTreatmentUseCase,
    private val deleteTreatmentUseCase: DeleteTreatmentUseCase,
    private val ownerPort: OwnerPort
) {
    @PostMapping("/api/beautishops/{shopId}/treatments")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTreatment(
        @PathVariable shopId: String,
        @RequestBody request: CreateTreatmentRequest,
        servletRequest: HttpServletRequest
    ): TreatmentResponse {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can create treatments")
        }

        val owner = ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = CreateTreatmentCommand(
            shopId = shopId,
            ownerId = owner.id.value.toString(),
            treatmentName = request.treatmentName,
            price = request.price,
            duration = request.duration,
            description = request.description
        )

        val treatment = createTreatmentUseCase.create(command)
        return TreatmentResponse.from(treatment)
    }

    @GetMapping("/api/treatments/{treatmentId}")
    fun getTreatment(@PathVariable treatmentId: String): TreatmentResponse {
        val command = GetTreatmentCommand(treatmentId = treatmentId)
        val treatment = getTreatmentUseCase.execute(command)
        return TreatmentResponse.from(treatment)
    }

    @GetMapping("/api/beautishops/{shopId}/treatments")
    fun listTreatments(@PathVariable shopId: String): List<TreatmentResponse> {
        val command = ListTreatmentsCommand(shopId = shopId)
        val treatments = listTreatmentsUseCase.execute(command)
        return treatments.map { TreatmentResponse.from(it) }
    }

    @PutMapping("/api/treatments/{treatmentId}")
    fun updateTreatment(
        @PathVariable treatmentId: String,
        @RequestBody request: UpdateTreatmentRequest,
        servletRequest: HttpServletRequest
    ): TreatmentResponse {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can update treatments")
        }

        val owner = ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = UpdateTreatmentCommand(
            treatmentId = treatmentId,
            ownerId = owner.id.value.toString(),
            treatmentName = request.treatmentName,
            price = request.price,
            duration = request.duration,
            description = request.description
        )

        val treatment = updateTreatmentUseCase.update(command)
        return TreatmentResponse.from(treatment)
    }

    @DeleteMapping("/api/treatments/{treatmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTreatment(
        @PathVariable treatmentId: String,
        servletRequest: HttpServletRequest
    ) {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can delete treatments")
        }

        val owner = ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = DeleteTreatmentCommand(
            treatmentId = treatmentId,
            ownerId = owner.id.value.toString()
        )

        deleteTreatmentUseCase.delete(command)
    }
}
