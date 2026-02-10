package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.exception.UnauthorizedReservationAccessException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.CompleteReservationCommand
import com.mad.jellomarkserver.reservation.port.driving.CompleteReservationUseCase
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory
import com.mad.jellomarkserver.usagehistory.port.driven.UsageHistoryPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.*

@Service
class CompleteReservationUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val beautishopPort: BeautishopPort,
    private val treatmentPort: TreatmentPort,
    private val usageHistoryPort: UsageHistoryPort,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val clock: Clock = Clock.systemUTC()
) : CompleteReservationUseCase {

    private val log = LoggerFactory.getLogger(CompleteReservationUseCaseImpl::class.java)

    @Transactional
    override fun execute(command: CompleteReservationCommand): Reservation {
        val reservationId = ReservationId.from(UUID.fromString(command.reservationId))
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))

        val reservation = reservationPort.findById(reservationId)
            ?: throw ReservationNotFoundException(command.reservationId)

        validateOwnerAccess(ownerId, reservation)

        val completed = reservation.complete(clock)
        val saved = reservationPort.save(completed)

        createUsageHistory(saved)
        sendCompletedNotification(saved)

        return saved
    }

    private fun createUsageHistory(reservation: Reservation) {
        val shop = beautishopPort.findById(reservation.shopId)
        val treatment = treatmentPort.findById(reservation.treatmentId)

        val usageHistory = UsageHistory.create(
            memberId = reservation.memberId,
            shopId = reservation.shopId,
            reservationId = reservation.id,
            shopName = shop?.name?.value ?: "",
            treatmentName = treatment?.name?.value ?: "",
            treatmentPrice = treatment?.price?.value ?: 0,
            treatmentDuration = treatment?.duration?.value ?: 0,
            completedAt = reservation.updatedAt,
            clock = clock
        )

        usageHistoryPort.save(usageHistory)
    }

    private fun sendCompletedNotification(reservation: Reservation) {
        try {
            val shop = beautishopPort.findById(reservation.shopId)
            val shopName = shop?.name?.value ?: "매장"

            sendNotificationUseCase.execute(
                SendNotificationCommand(
                    userId = reservation.memberId.value.toString(),
                    userRole = "MEMBER",
                    title = "시술이 완료되었습니다",
                    body = "$shopName - 리뷰를 남겨주세요",
                    type = "RESERVATION_COMPLETED",
                    data = mapOf("reservationId" to reservation.id.value.toString())
                )
            )
        } catch (e: Exception) {
            log.warn("Failed to send RESERVATION_COMPLETED notification: {}", e.message)
        }
    }

    private fun validateOwnerAccess(ownerId: OwnerId, reservation: Reservation) {
        val ownerShops = beautishopPort.findByOwnerId(ownerId)
        val ownsShop = ownerShops.any { it.id == reservation.shopId }
        if (!ownsShop) {
            throw UnauthorizedReservationAccessException(
                reservation.id.value.toString(), ownerId.value.toString()
            )
        }
    }
}
