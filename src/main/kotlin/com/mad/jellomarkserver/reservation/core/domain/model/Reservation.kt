package com.mad.jellomarkserver.reservation.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.exception.InvalidReservationStatusTransitionException
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class Reservation private constructor(
    val id: ReservationId,
    val shopId: ShopId,
    val memberId: MemberId,
    val treatmentId: TreatmentId,
    val reservationDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val status: ReservationStatus,
    val memo: ReservationMemo?,
    val rejectionReason: RejectionReason?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(
            shopId: ShopId,
            memberId: MemberId,
            treatmentId: TreatmentId,
            reservationDate: LocalDate,
            startTime: LocalTime,
            endTime: LocalTime,
            memo: ReservationMemo?,
            clock: Clock = Clock.systemUTC()
        ): Reservation {
            val now = Instant.now(clock)
            return Reservation(
                id = ReservationId.new(),
                shopId = shopId,
                memberId = memberId,
                treatmentId = treatmentId,
                reservationDate = reservationDate,
                startTime = startTime,
                endTime = endTime,
                status = ReservationStatus.PENDING,
                memo = memo,
                rejectionReason = null,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstruct(
            id: ReservationId,
            shopId: ShopId,
            memberId: MemberId,
            treatmentId: TreatmentId,
            reservationDate: LocalDate,
            startTime: LocalTime,
            endTime: LocalTime,
            status: ReservationStatus,
            memo: ReservationMemo?,
            rejectionReason: RejectionReason?,
            createdAt: Instant,
            updatedAt: Instant
        ): Reservation {
            return Reservation(
                id = id,
                shopId = shopId,
                memberId = memberId,
                treatmentId = treatmentId,
                reservationDate = reservationDate,
                startTime = startTime,
                endTime = endTime,
                status = status,
                memo = memo,
                rejectionReason = rejectionReason,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun confirm(clock: Clock = Clock.systemUTC()): Reservation {
        validateTransition(ReservationStatus.CONFIRMED)
        return copy(status = ReservationStatus.CONFIRMED, updatedAt = Instant.now(clock))
    }

    fun reject(reason: RejectionReason, clock: Clock = Clock.systemUTC()): Reservation {
        validateTransition(ReservationStatus.REJECTED)
        return copy(
            status = ReservationStatus.REJECTED,
            rejectionReason = reason,
            updatedAt = Instant.now(clock)
        )
    }

    fun cancel(clock: Clock = Clock.systemUTC()): Reservation {
        validateTransition(ReservationStatus.CANCELLED)
        return copy(status = ReservationStatus.CANCELLED, updatedAt = Instant.now(clock))
    }

    fun complete(clock: Clock = Clock.systemUTC()): Reservation {
        validateTransition(ReservationStatus.COMPLETED)
        return copy(status = ReservationStatus.COMPLETED, updatedAt = Instant.now(clock))
    }

    fun noShow(clock: Clock = Clock.systemUTC()): Reservation {
        validateTransition(ReservationStatus.NO_SHOW)
        return copy(status = ReservationStatus.NO_SHOW, updatedAt = Instant.now(clock))
    }

    fun isOwnedByMember(memberId: MemberId): Boolean {
        return this.memberId == memberId
    }

    fun belongsToShop(shopId: ShopId): Boolean {
        return this.shopId == shopId
    }

    private fun validateTransition(target: ReservationStatus) {
        if (!status.canTransitionTo(target)) {
            throw InvalidReservationStatusTransitionException(status.name, target.name)
        }
    }

    private fun copy(
        status: ReservationStatus = this.status,
        rejectionReason: RejectionReason? = this.rejectionReason,
        updatedAt: Instant = this.updatedAt
    ): Reservation {
        return Reservation(
            id = this.id,
            shopId = this.shopId,
            memberId = this.memberId,
            treatmentId = this.treatmentId,
            reservationDate = this.reservationDate,
            startTime = this.startTime,
            endTime = this.endTime,
            status = status,
            memo = this.memo,
            rejectionReason = rejectionReason,
            createdAt = this.createdAt,
            updatedAt = updatedAt
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Reservation) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
