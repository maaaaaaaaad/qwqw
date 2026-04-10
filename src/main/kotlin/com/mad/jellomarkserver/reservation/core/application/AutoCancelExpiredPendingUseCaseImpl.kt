package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.AutoCancelExpiredPendingUseCase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Service
class AutoCancelExpiredPendingUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val clock: Clock = Clock.system(KST_ZONE)
) : AutoCancelExpiredPendingUseCase {

    private val log = LoggerFactory.getLogger(AutoCancelExpiredPendingUseCaseImpl::class.java)

    @Transactional
    override fun execute() {
        val today = LocalDate.now(clock)
        val now = LocalTime.now(clock)

        val pendingReservations = reservationPort.findByStatusAndDate(ReservationStatus.PENDING, today)
        if (pendingReservations.isEmpty()) return

        val expired = pendingReservations.filter { it.startTime.isBefore(now) }
        if (expired.isEmpty()) return

        for (reservation in expired) {
            val cancelled = reservation.cancel(clock)
            reservationPort.save(cancelled)

            sendNotificationUseCase.execute(
                SendNotificationCommand(
                    userId = reservation.memberId.value.toString(),
                    userRole = "MEMBER",
                    title = "예약이 자동 취소되었습니다",
                    body = "사장님의 미확정으로 예약이 취소되었습니다. 불편을 드려 죄송합니다.",
                    type = "RESERVATION_CANCELLED",
                    data = mapOf(
                        "reservationId" to reservation.id.value.toString(),
                        "shopId" to reservation.shopId.value.toString()
                    )
                )
            )

            log.info("Auto-cancelled expired pending reservation {}", reservation.id.value)
        }

        log.info("Auto-cancelled {} expired pending reservations", expired.size)
    }

    companion object {
        private val KST_ZONE = ZoneId.of("Asia/Seoul")
    }
}
