package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.externalshop.core.application.ExternalShopService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class ExternalShopController(
    private val externalShopService: ExternalShopService
) {

    @GetMapping("/api/external-shops")
    fun getNearbyExternalShops(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(defaultValue = "5.0") radiusKm: Double
    ): List<ExternalShopResponse> {
        return externalShopService.findNearby(latitude, longitude, radiusKm)
            .map { entity ->
                ExternalShopResponse(
                    id = entity.id.toString(),
                    name = entity.name,
                    address = entity.address,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    category = entity.category,
                    phoneNumber = entity.phoneNumber
                )
            }
    }

    @PostMapping("/api/external-shops/sync")
    @ResponseStatus(HttpStatus.OK)
    fun syncExternalShops(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(defaultValue = "5000") radiusMeters: Int
    ): Map<String, Any> {
        val count = externalShopService.syncFromPublicData(latitude, longitude, radiusMeters)
        return mapOf("synced" to count)
    }
}

data class ExternalShopResponse(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val phoneNumber: String?
)
