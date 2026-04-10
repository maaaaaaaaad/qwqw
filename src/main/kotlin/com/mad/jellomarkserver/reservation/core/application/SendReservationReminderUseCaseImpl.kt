package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.SendReservationReminderUseCase
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Service
class SendReservationReminderUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val beautishopPort: BeautishopPort,
    private val treatmentPort: TreatmentPort,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val clock: Clock = Clock.system(KST_ZONE)
) : SendReservationReminderUseCase {

    private val log = LoggerFactory.getLogger(SendReservationReminderUseCaseImpl::class.java)
    private val notifiedReservations = mutableSetOf<String>()

    override fun execute() {
        val today = LocalDate.now(clock)
        val now = LocalTime.now(clock)

        val confirmedReservations = reservationPort.findByStatusAndDate(ReservationStatus.CONFIRMED, today)
        if (confirmedReservations.isEmpty()) return

        val upcomingReservations = confirmedReservations.filter { reservation ->
            val minutesUntilStart = java.time.Duration.between(now, reservation.startTime).toMinutes()
            minutesUntilStart in 0..REMINDER_MINUTES
        }

        if (upcomingReservations.isEmpty()) return

        val shopIds = upcomingReservations.map { it.shopId }.distinct()
        val shopsById = beautishopPort.findByIds(shopIds).associateBy { it.id }

        for (reservation in upcomingReservations) {
            val deduplicationKey = "${reservation.id.value}:$today"
            if (notifiedReservations.contains(deduplicationKey)) continue

            val shop = shopsById[reservation.shopId] ?: continue
            val shopName = shop.name.value
            val timeText = "${reservation.startTime.hour}시 ${reservation.startTime.minute}분"

            val treatment = treatmentPort.findById(reservation.treatmentId)
            val treatmentName = treatment?.name?.value ?: "시술"

            notifyOwner(reservation, shopName, timeText, treatmentName)
            notifyMember(reservation, shopName, timeText, treatmentName)

            notifiedReservations.add(deduplicationKey)
            log.info("Sent reservation reminder for reservation {}", reservation.id.value)
        }
    }

    private fun notifyOwner(reservation: Reservation, shopName: String, timeText: String, treatmentName: String) {
        val ownerId = beautishopPort.findOwnerIdByShopId(reservation.shopId) ?: return

        sendNotificationUseCase.execute(
            SendNotificationCommand(
                userId = ownerId.value.toString(),
                userRole = "OWNER",
                title = "예약 리마인더",
                body = "$timeText $treatmentName 예약이 곧 시작됩니다",
                type = "RESERVATION_REMINDER",
                data = mapOf(
                    "reservationId" to reservation.id.value.toString(),
                    "shopId" to reservation.shopId.value.toString()
                )
            )
        )
    }

    private fun notifyMember(reservation: Reservation, shopName: String, timeText: String, treatmentName: String) {
        sendNotificationUseCase.execute(
            SendNotificationCommand(
                userId = reservation.memberId.value.toString(),
                userRole = "MEMBER",
                title = "예약 리마인더",
                body = "$shopName $timeText $treatmentName 예약이 곧 시작됩니다",
                type = "RESERVATION_REMINDER",
                data = mapOf(
                    "reservationId" to reservation.id.value.toString(),
                    "shopId" to reservation.shopId.value.toString()
                )
            )
        )
    }

    companion object {
        private val KST_ZONE = ZoneId.of("Asia/Seoul")
        private const val REMINDER_MINUTES = 60L
    }
}
