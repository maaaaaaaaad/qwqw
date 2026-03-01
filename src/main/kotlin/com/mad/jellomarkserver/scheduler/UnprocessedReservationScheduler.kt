package com.mad.jellomarkserver.scheduler

import com.mad.jellomarkserver.reservation.port.driving.NotifyUnprocessedReservationsUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UnprocessedReservationScheduler(
    private val notifyUnprocessedReservationsUseCase: NotifyUnprocessedReservationsUseCase
) {

    private val log = LoggerFactory.getLogger(UnprocessedReservationScheduler::class.java)

    @Scheduled(cron = "0 0/30 * * * *", zone = "Asia/Seoul")
    fun checkUnprocessedReservations() {
        log.info("Starting unprocessed reservation check")
        notifyUnprocessedReservationsUseCase.execute()
        log.info("Completed unprocessed reservation check")
    }
}
