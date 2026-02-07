package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import java.time.Instant

data class ReservationResponse(
    val id: String,
    val shopId: String,
    val memberId: String,
    val treatmentId: String,
    val shopName: String?,
    val treatmentName: String?,
    val treatmentPrice: Int?,
    val treatmentDuration: Int?,
    val memberNickname: String?,
    val reservationDate: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val memo: String?,
    val rejectionReason: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(
            reservation: Reservation,
            shopName: String? = null,
            treatmentName: String? = null,
            treatmentPrice: Int? = null,
            treatmentDuration: Int? = null,
            memberNickname: String? = null
        ): ReservationResponse {
            return ReservationResponse(
                id = reservation.id.value.toString(),
                shopId = reservation.shopId.value.toString(),
                memberId = reservation.memberId.value.toString(),
                treatmentId = reservation.treatmentId.value.toString(),
                shopName = shopName,
                treatmentName = treatmentName,
                treatmentPrice = treatmentPrice,
                treatmentDuration = treatmentDuration,
                memberNickname = memberNickname,
                reservationDate = reservation.reservationDate.toString(),
                startTime = reservation.startTime.toString(),
                endTime = reservation.endTime.toString(),
                status = reservation.status.name,
                memo = reservation.memo?.value,
                rejectionReason = reservation.rejectionReason?.value,
                createdAt = reservation.createdAt,
                updatedAt = reservation.updatedAt
            )
        }
    }
}
