package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.exception.UnauthorizedReservationAccessException
import com.mad.jellomarkserver.reservation.core.domain.model.RejectionReason
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.RejectReservationCommand
import com.mad.jellomarkserver.reservation.port.driving.RejectReservationUseCase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RejectReservationUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val beautishopPort: BeautishopPort,
    private val sendNotificationUseCase: SendNotificationUseCase
) : RejectReservationUseCase {

    private val log = LoggerFactory.getLogger(RejectReservationUseCaseImpl::class.java)

    @Transactional
    override fun execute(command: RejectReservationCommand): Reservation {
        val reservationId = ReservationId.from(UUID.fromString(command.reservationId))
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))

        val reservation = reservationPort.findById(reservationId)
            ?: throw ReservationNotFoundException(command.reservationId)

        validateOwnerAccess(ownerId, reservation)

        val reason = RejectionReason.of(command.rejectionReason)
        val rejected = reservation.reject(reason)
        val saved = reservationPort.save(rejected)

        sendRejectedNotification(saved)

        return saved
    }

    private fun sendRejectedNotification(reservation: Reservation) {
        try {
            val shop = beautishopPort.findById(reservation.shopId)
            val shopName = shop?.name?.value ?: "매장"
            val rejectionReason = reservation.rejectionReason?.value ?: ""

            sendNotificationUseCase.execute(
                SendNotificationCommand(
                    userId = reservation.memberId.value.toString(),
                    userRole = "MEMBER",
                    title = "예약이 거절되었습니다",
                    body = "$shopName - $rejectionReason",
                    type = "RESERVATION_REJECTED",
                    data = mapOf("reservationId" to reservation.id.value.toString())
                )
            )
        } catch (e: Exception) {
            log.warn("Failed to send RESERVATION_REJECTED notification: {}", e.message)
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
