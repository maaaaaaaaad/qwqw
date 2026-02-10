package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.exception.UnauthorizedReservationAccessException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.CancelReservationCommand
import com.mad.jellomarkserver.reservation.port.driving.CancelReservationUseCase
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CancelReservationUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val beautishopPort: BeautishopPort,
    private val treatmentPort: TreatmentPort,
    private val memberPort: MemberPort,
    private val sendNotificationUseCase: SendNotificationUseCase
) : CancelReservationUseCase {

    private val log = LoggerFactory.getLogger(CancelReservationUseCaseImpl::class.java)

    @Transactional
    override fun execute(command: CancelReservationCommand): Reservation {
        val reservationId = ReservationId.from(UUID.fromString(command.reservationId))
        val memberId = MemberId.from(UUID.fromString(command.memberId))

        val reservation = reservationPort.findById(reservationId)
            ?: throw ReservationNotFoundException(command.reservationId)

        if (!reservation.isOwnedByMember(memberId)) {
            throw UnauthorizedReservationAccessException(
                command.reservationId, command.memberId
            )
        }

        val cancelled = reservation.cancel()
        val saved = reservationPort.save(cancelled)

        sendCancelledNotification(saved)

        return saved
    }

    private fun sendCancelledNotification(reservation: Reservation) {
        try {
            val ownerId = beautishopPort.findOwnerIdByShopId(reservation.shopId) ?: return
            val member = memberPort.findById(reservation.memberId)
            val treatment = treatmentPort.findById(reservation.treatmentId)
            val memberNickname = member?.memberNickname?.value ?: "회원"
            val treatmentName = treatment?.name?.value ?: "시술"

            sendNotificationUseCase.execute(
                SendNotificationCommand(
                    userId = ownerId.value.toString(),
                    userRole = "OWNER",
                    title = "예약이 취소되었습니다",
                    body = "$memberNickname - $treatmentName ${reservation.reservationDate} ${reservation.startTime}",
                    type = "RESERVATION_CANCELLED",
                    data = mapOf("reservationId" to reservation.id.value.toString())
                )
            )
        } catch (e: Exception) {
            log.warn("Failed to send RESERVATION_CANCELLED notification: {}", e.message)
        }
    }
}
