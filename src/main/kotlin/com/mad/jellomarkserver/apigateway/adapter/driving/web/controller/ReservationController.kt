package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateReservationRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.RejectReservationRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.AvailableDatesResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.AvailableSlotsResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.ReservationResponse
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.port.driving.*
import com.mad.jellomarkserver.reservation.port.driving.GetAvailableDatesQuery
import com.mad.jellomarkserver.reservation.port.driving.GetAvailableDatesUseCase
import com.mad.jellomarkserver.reservation.port.driving.GetAvailableSlotsQuery
import com.mad.jellomarkserver.reservation.port.driving.GetAvailableSlotsUseCase
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ReservationController(
    private val createReservationUseCase: CreateReservationUseCase,
    private val getReservationUseCase: GetReservationUseCase,
    private val listMemberReservationsUseCase: ListMemberReservationsUseCase,
    private val listShopReservationsUseCase: ListShopReservationsUseCase,
    private val confirmReservationUseCase: ConfirmReservationUseCase,
    private val rejectReservationUseCase: RejectReservationUseCase,
    private val cancelReservationUseCase: CancelReservationUseCase,
    private val completeReservationUseCase: CompleteReservationUseCase,
    private val noShowReservationUseCase: NoShowReservationUseCase,
    private val getAvailableSlotsUseCase: GetAvailableSlotsUseCase,
    private val getAvailableDatesUseCase: GetAvailableDatesUseCase,
    private val memberPort: MemberPort,
    private val ownerPort: OwnerPort,
    private val beautishopPort: BeautishopPort,
    private val treatmentPort: TreatmentPort
) {

    @PostMapping("/api/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    fun createReservation(
        @RequestBody request: CreateReservationRequest,
        servletRequest: HttpServletRequest
    ): ReservationResponse {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can create reservations")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = CreateReservationCommand(
            shopId = request.shopId,
            memberId = member.id.value.toString(),
            treatmentId = request.treatmentId,
            reservationDate = request.reservationDate,
            startTime = request.startTime,
            memo = request.memo
        )

        val reservation = createReservationUseCase.execute(command)
        return enrichResponse(reservation)
    }

    @GetMapping("/api/reservations/me")
    fun getMyReservations(servletRequest: HttpServletRequest): List<ReservationResponse> {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can view their reservations")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = ListMemberReservationsCommand(member.id.value.toString())
        val reservations = listMemberReservationsUseCase.execute(command)
        return enrichResponses(reservations)
    }

    @GetMapping("/api/reservations/{id}")
    fun getReservation(
        @PathVariable id: String,
        servletRequest: HttpServletRequest
    ): ReservationResponse {
        val command = GetReservationCommand(id)
        val reservation = getReservationUseCase.execute(command)
        return enrichResponse(reservation)
    }

    @PatchMapping("/api/reservations/{id}/cancel")
    fun cancelReservation(
        @PathVariable id: String,
        servletRequest: HttpServletRequest
    ): ReservationResponse {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can cancel reservations")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = CancelReservationCommand(
            reservationId = id,
            memberId = member.id.value.toString()
        )

        val reservation = cancelReservationUseCase.execute(command)
        return enrichResponse(reservation)
    }

    @GetMapping("/api/beautishops/{shopId}/reservations")
    fun listShopReservations(
        @PathVariable shopId: String,
        servletRequest: HttpServletRequest
    ): List<ReservationResponse> {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can view shop reservations")
        }

        ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)

        val command = ListShopReservationsCommand(shopId)
        val reservations = listShopReservationsUseCase.execute(command)
        return enrichResponses(reservations)
    }

    @PatchMapping("/api/reservations/{id}/confirm")
    fun confirmReservation(
        @PathVariable id: String,
        servletRequest: HttpServletRequest
    ): ReservationResponse {
        val owner = resolveOwner(servletRequest)

        val command = ConfirmReservationCommand(
            reservationId = id,
            ownerId = owner.id.value.toString()
        )

        val reservation = confirmReservationUseCase.execute(command)
        return enrichResponse(reservation)
    }

    @PatchMapping("/api/reservations/{id}/reject")
    fun rejectReservation(
        @PathVariable id: String,
        @RequestBody request: RejectReservationRequest,
        servletRequest: HttpServletRequest
    ): ReservationResponse {
        val owner = resolveOwner(servletRequest)

        val command = RejectReservationCommand(
            reservationId = id,
            ownerId = owner.id.value.toString(),
            rejectionReason = request.rejectionReason
        )

        val reservation = rejectReservationUseCase.execute(command)
        return enrichResponse(reservation)
    }

    @PatchMapping("/api/reservations/{id}/complete")
    fun completeReservation(
        @PathVariable id: String,
        servletRequest: HttpServletRequest
    ): ReservationResponse {
        val owner = resolveOwner(servletRequest)

        val command = CompleteReservationCommand(
            reservationId = id,
            ownerId = owner.id.value.toString()
        )

        val reservation = completeReservationUseCase.execute(command)
        return enrichResponse(reservation)
    }

    @PatchMapping("/api/reservations/{id}/no-show")
    fun noShowReservation(
        @PathVariable id: String,
        servletRequest: HttpServletRequest
    ): ReservationResponse {
        val owner = resolveOwner(servletRequest)

        val command = NoShowReservationCommand(
            reservationId = id,
            ownerId = owner.id.value.toString()
        )

        val reservation = noShowReservationUseCase.execute(command)
        return enrichResponse(reservation)
    }

    @GetMapping("/api/beautishops/{shopId}/available-dates")
    fun getAvailableDates(
        @PathVariable shopId: String,
        @RequestParam yearMonth: String,
        @RequestParam treatmentId: String
    ): AvailableDatesResponse {
        val query = GetAvailableDatesQuery(
            shopId = shopId,
            treatmentId = treatmentId,
            yearMonth = yearMonth
        )
        val result = getAvailableDatesUseCase.execute(query)
        return AvailableDatesResponse.from(result)
    }

    @GetMapping("/api/beautishops/{shopId}/available-slots")
    fun getAvailableSlots(
        @PathVariable shopId: String,
        @RequestParam date: String,
        @RequestParam treatmentId: String
    ): AvailableSlotsResponse {
        val query = GetAvailableSlotsQuery(
            shopId = shopId,
            treatmentId = treatmentId,
            date = date
        )
        val result = getAvailableSlotsUseCase.execute(query)
        return AvailableSlotsResponse.from(result)
    }

    private fun resolveOwner(servletRequest: HttpServletRequest): com.mad.jellomarkserver.owner.core.domain.model.Owner {
        val email = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "OWNER") {
            throw IllegalStateException("Only owners can perform this action")
        }

        return ownerPort.findByEmail(OwnerEmail.of(email))
            ?: throw OwnerNotFoundException(email)
    }

    private fun enrichResponse(reservation: Reservation): ReservationResponse {
        val shop = beautishopPort.findById(reservation.shopId)
        val treatment = treatmentPort.findById(reservation.treatmentId)
        val member = memberPort.findById(reservation.memberId)

        return ReservationResponse.from(
            reservation = reservation,
            shopName = shop?.name?.value,
            treatmentName = treatment?.name?.value,
            treatmentPrice = treatment?.price?.value,
            treatmentDuration = treatment?.duration?.value,
            memberNickname = member?.memberNickname?.value
        )
    }

    private fun enrichResponses(reservations: List<Reservation>): List<ReservationResponse> {
        if (reservations.isEmpty()) return emptyList()

        val shopIds = reservations.map { it.shopId }.distinct()
        val treatmentIds = reservations.map { it.treatmentId }.distinct()
        val memberIds = reservations.map { MemberId.from(it.memberId.value) }.distinct()

        val shops = shopIds.mapNotNull { beautishopPort.findById(it) }
            .associateBy { it.id }
        val treatments = treatmentIds.mapNotNull { treatmentPort.findById(it) }
            .associateBy { it.id }
        val members = memberPort.findByIds(memberIds)
            .associateBy { it.id }

        return reservations.map { reservation ->
            val shop = shops[reservation.shopId]
            val treatment = treatments[reservation.treatmentId]
            val member = members[reservation.memberId]

            ReservationResponse.from(
                reservation = reservation,
                shopName = shop?.name?.value,
                treatmentName = treatment?.name?.value,
                treatmentPrice = treatment?.price?.value,
                treatmentDuration = treatment?.duration?.value,
                memberNickname = member?.memberNickname?.value
            )
        }
    }
}
