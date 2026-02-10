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
import com.mad.jellomarkserver.reservation.port.driving.ConfirmReservationCommand
import com.mad.jellomarkserver.reservation.port.driving.ConfirmReservationUseCase
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ConfirmReservationUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val beautishopPort: BeautishopPort,
    private val treatmentPort: TreatmentPort,
    private val sendNotificationUseCase: SendNotificationUseCase
) : ConfirmReservationUseCase {

    private val log = LoggerFactory.getLogger(ConfirmReservationUseCaseImpl::class.java)

    @Transactional
    override fun execute(command: ConfirmReservationCommand): Reservation {
        val reservationId = ReservationId.from(UUID.fromString(command.reservationId))
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))

        val reservation = reservationPort.findById(reservationId)
            ?: throw ReservationNotFoundException(command.reservationId)

        validateOwnerAccess(ownerId, reservation)

        val confirmed = reservation.confirm()
        val saved = reservationPort.save(confirmed)

        sendConfirmedNotification(saved)

        return saved
    }

    private fun sendConfirmedNotification(reservation: Reservation) {
        try {
            val shop = beautishopPort.findById(reservation.shopId)
            val treatment = treatmentPort.findById(reservation.treatmentId)
            val shopName = shop?.name?.value ?: "매장"
            val treatmentName = treatment?.name?.value ?: "시술"

            sendNotificationUseCase.execute(
                SendNotificationCommand(
                    userId = reservation.memberId.value.toString(),
                    userRole = "MEMBER",
                    title = "예약이 확정되었습니다",
                    body = "$shopName - $treatmentName ${reservation.reservationDate} ${reservation.startTime}",
                    type = "RESERVATION_CONFIRMED",
                    data = mapOf(
                        "reservationId" to reservation.id.value.toString(),
                        "reservationDate" to reservation.reservationDate.toString(),
                        "startTime" to reservation.startTime.toString(),
                        "shopName" to shopName,
                        "treatmentName" to treatmentName
                    )
                )
            )
        } catch (e: Exception) {
            log.warn("Failed to send RESERVATION_CONFIRMED notification: {}", e.message)
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
