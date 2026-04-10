package com.mad.jellomarkserver.scheduler

import com.mad.jellomarkserver.reservation.port.driving.SendReservationReminderUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ReservationReminderScheduler(
    private val sendReservationReminderUseCase: SendReservationReminderUseCase
) {

    private val log = LoggerFactory.getLogger(ReservationReminderScheduler::class.java)

    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    fun checkUpcomingReservations() {
        log.info("Starting reservation reminder check")
        sendReservationReminderUseCase.execute()
        log.info("Completed reservation reminder check")
    }
}
