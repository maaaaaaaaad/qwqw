package com.mad.jellomarkserver.beautishop.core.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test

class GpsDistanceCalculatorTest {

    @Test
    fun `should return 0 for same coordinates`() {
        val distance = GpsDistanceCalculator.calculateDistanceKm(
            lat1 = 37.5665,
            lon1 = 126.9780,
            lat2 = 37.5665,
            lon2 = 126.9780
        )

        assertThat(distance).isEqualTo(0.0)
    }

    @Test
    fun `should calculate distance between Seoul and Busan approximately 325km`() {
        val distance = GpsDistanceCalculator.calculateDistanceKm(
            lat1 = 37.5665,
            lon1 = 126.9780,
            lat2 = 35.1796,
            lon2 = 129.0756
        )

        assertThat(distance).isCloseTo(325.0, Offset.offset(10.0))
    }

    @Test
    fun `should calculate distance between Gangnam and Hongdae approximately 10km`() {
        val distance = GpsDistanceCalculator.calculateDistanceKm(
            lat1 = 37.4979,
            lon1 = 127.0276,
            lat2 = 37.5563,
            lon2 = 126.9220
        )

        assertThat(distance).isCloseTo(11.0, Offset.offset(2.0))
    }

    @Test
    fun `should be symmetric`() {
        val distance1 = GpsDistanceCalculator.calculateDistanceKm(
            lat1 = 37.5665,
            lon1 = 126.9780,
            lat2 = 35.1796,
            lon2 = 129.0756
        )

        val distance2 = GpsDistanceCalculator.calculateDistanceKm(
            lat1 = 35.1796,
            lon1 = 129.0756,
            lat2 = 37.5665,
            lon2 = 126.9780
        )

        assertThat(distance1).isEqualTo(distance2)
    }
}
