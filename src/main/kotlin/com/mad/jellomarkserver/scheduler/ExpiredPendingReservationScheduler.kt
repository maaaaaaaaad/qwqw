package com.mad.jellomarkserver.scheduler

import com.mad.jellomarkserver.reservation.port.driving.AutoCancelExpiredPendingUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ExpiredPendingReservationScheduler(
    private val autoCancelExpiredPendingUseCase: AutoCancelExpiredPendingUseCase
) {

    private val log = LoggerFactory.getLogger(ExpiredPendingReservationScheduler::class.java)

    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Seoul")
    fun cancelExpiredPendingReservations() {
        log.info("Starting expired pending reservation check")
        autoCancelExpiredPendingUseCase.execute()
        log.info("Completed expired pending reservation check")
    }
}
