package com.mad.jellomarkserver.externalshop.adapter.driven.api

import com.mad.jellomarkserver.externalshop.core.application.ExternalShopService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ExternalShopSyncScheduler(
    private val externalShopService: ExternalShopService
) {
    private val log = LoggerFactory.getLogger(ExternalShopSyncScheduler::class.java)

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    fun syncNailShops() {
        log.info("Starting daily external shop sync")

        var totalSynced = 0
        for ((name, lat, lng) in MAJOR_CITIES) {
            try {
                val count = externalShopService.syncFromPublicData(lat, lng, RADIUS_METERS)
                totalSynced += count
                log.info("Synced {} shops for {}", count, name)
            } catch (e: Exception) {
                log.error("Failed to sync shops for {}: {}", name, e.message)
            }
        }

        log.info("Completed daily external shop sync. Total: {} shops", totalSynced)
    }

    companion object {
        private const val RADIUS_METERS = 20000

        private val MAJOR_CITIES = listOf(
            Triple("서울 강남", 37.4979, 127.0276),
            Triple("서울 종로", 37.5704, 126.9922),
            Triple("서울 마포", 37.5665, 126.9018),
            Triple("부산 해운대", 35.1631, 129.1638),
            Triple("인천 부평", 37.5074, 126.7219),
            Triple("대구 중구", 35.8714, 128.6014),
            Triple("대전 서구", 36.3551, 127.3838),
            Triple("광주 서구", 35.1526, 126.8896),
            Triple("수원 영통", 37.2636, 127.0286),
            Triple("성남 분당", 37.3828, 127.1193),
        )
    }
}
