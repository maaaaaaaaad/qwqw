package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.reservation.core.domain.exception.PastReservationException
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationTimeConflictException
import com.mad.jellomarkserver.reservation.core.domain.exception.TreatmentNotInShopException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationMemo
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.CreateReservationCommand
import com.mad.jellomarkserver.reservation.port.driving.CreateReservationUseCase
import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Service
class CreateReservationUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val treatmentPort: TreatmentPort,
    private val beautishopPort: BeautishopPort,
    private val memberPort: MemberPort,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val clock: Clock = Clock.systemUTC()
) : CreateReservationUseCase {

    private val log = LoggerFactory.getLogger(CreateReservationUseCaseImpl::class.java)

    @Transactional
    override fun execute(command: CreateReservationCommand): Reservation {
        val shopId = ShopId.from(UUID.fromString(command.shopId))
        val memberId = MemberId.from(UUID.fromString(command.memberId))
        val treatmentId = TreatmentId.from(UUID.fromString(command.treatmentId))
        val reservationDate = LocalDate.parse(command.reservationDate)
        val startTime = LocalTime.parse(command.startTime)

        if (reservationDate.isBefore(LocalDate.now(clock))) {
            throw PastReservationException(command.reservationDate)
        }

        val treatment = treatmentPort.findById(treatmentId)
            ?: throw TreatmentNotFoundException(command.treatmentId)

        if (treatment.shopId != shopId) {
            throw TreatmentNotInShopException(command.treatmentId, command.shopId)
        }

        val endTime = startTime.plusMinutes(treatment.duration.value.toLong())

        if (reservationPort.existsOverlapping(shopId, reservationDate, startTime, endTime)) {
            throw ReservationTimeConflictException(
                command.shopId, command.reservationDate, startTime.toString(), endTime.toString()
            )
        }

        val reservation = Reservation.create(
            shopId = shopId,
            memberId = memberId,
            treatmentId = treatmentId,
            reservationDate = reservationDate,
            startTime = startTime,
            endTime = endTime,
            memo = ReservationMemo.ofNullable(command.memo),
            clock = clock
        )

        val saved = reservationPort.save(reservation)

        sendCreatedNotification(saved, treatment.name.value)

        return saved
    }

    private fun sendCreatedNotification(reservation: Reservation, treatmentName: String) {
        try {
            val ownerId = beautishopPort.findOwnerIdByShopId(reservation.shopId) ?: return
            val member = memberPort.findById(reservation.memberId)
            val memberNickname = member?.memberNickname?.value ?: "회원"

            sendNotificationUseCase.execute(
                SendNotificationCommand(
                    userId = ownerId.value.toString(),
                    userRole = "OWNER",
                    title = "새 예약이 들어왔습니다",
                    body = "$memberNickname - $treatmentName ${reservation.reservationDate} ${reservation.startTime}",
                    type = "RESERVATION_CREATED",
                    data = mapOf("reservationId" to reservation.id.value.toString())
                )
            )
        } catch (e: Exception) {
            log.warn("Failed to send RESERVATION_CREATED notification: {}", e.message)
        }
    }
}
