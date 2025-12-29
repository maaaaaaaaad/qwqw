package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopGPSException

data class ShopGPS private constructor(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        private const val MIN_LATITUDE = -90.0
        private const val MAX_LATITUDE = 90.0
        private const val MIN_LONGITUDE = -180.0
        private const val MAX_LONGITUDE = 180.0

        fun of(latitude: Double, longitude: Double): ShopGPS {
            try {
                require(latitude in MIN_LATITUDE..MAX_LATITUDE) {
                    "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE"
                }
                require(longitude in MIN_LONGITUDE..MAX_LONGITUDE) {
                    "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE"
                }
                return ShopGPS(latitude, longitude)
            } catch (ex: IllegalArgumentException) {
                throw InvalidShopGPSException("latitude=$latitude, longitude=$longitude - ${ex.message}")
            }
        }
    }
}
