package com.mad.jellomarkserver.externalshop.core.application

import com.mad.jellomarkserver.externalshop.adapter.driven.api.PublicDataApiClient
import com.mad.jellomarkserver.externalshop.adapter.driven.persistence.entity.ExternalShopJpaEntity
import com.mad.jellomarkserver.externalshop.adapter.driven.persistence.repository.ExternalShopJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ExternalShopService(
    private val repository: ExternalShopJpaRepository,
    private val publicDataApiClient: PublicDataApiClient
) {
    private val log = LoggerFactory.getLogger(ExternalShopService::class.java)

    fun findNearby(latitude: Double, longitude: Double, radiusKm: Double): List<ExternalShopJpaEntity> {
        val latDelta = radiusKm / LAT_KM_RATIO
        val lngDelta = radiusKm / (LNG_KM_RATIO * Math.cos(Math.toRadians(latitude)))

        return repository.findInBoundingBox(
            minLat = latitude - latDelta,
            maxLat = latitude + latDelta,
            minLng = longitude - lngDelta,
            maxLng = longitude + lngDelta
        )
    }

    @Transactional
    fun syncFromPublicData(latitude: Double, longitude: Double, radiusMeters: Int = 5000): Int {
        val shops = publicDataApiClient.fetchNailShopsInRadius(latitude, longitude, radiusMeters)
        var upsertCount = 0

        for (shop in shops) {
            val existing = repository.findByExternalId(shop.externalId)
            if (existing != null) {
                existing.name = shop.name
                existing.address = shop.address
                existing.latitude = shop.latitude
                existing.longitude = shop.longitude
                existing.category = shop.category
                existing.phoneNumber = shop.phoneNumber
                existing.lastUpdated = Instant.now()
                repository.save(existing)
            } else {
                repository.save(
                    ExternalShopJpaEntity(
                        externalId = shop.externalId,
                        name = shop.name,
                        address = shop.address,
                        latitude = shop.latitude,
                        longitude = shop.longitude,
                        category = shop.category,
                        phoneNumber = shop.phoneNumber,
                        lastUpdated = Instant.now(),
                        createdAt = Instant.now()
                    )
                )
            }
            upsertCount++
        }

        log.info("Synced {} external shops from public data (lat={}, lng={})", upsertCount, latitude, longitude)
        return upsertCount
    }

    companion object {
        private const val LAT_KM_RATIO = 111.0
        private const val LNG_KM_RATIO = 111.0
    }
}
