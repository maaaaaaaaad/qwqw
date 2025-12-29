package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopGPSException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ShopGPSTest {

    @Test
    fun `should create ShopGPS with valid coordinates`() {
        val gps = ShopGPS.of(37.5665, 126.9780)
        assertEquals(37.5665, gps.latitude)
        assertEquals(126.9780, gps.longitude)
    }

    @Test
    fun `should create ShopGPS with minimum latitude`() {
        val gps = ShopGPS.of(-90.0, 0.0)
        assertEquals(-90.0, gps.latitude)
        assertEquals(0.0, gps.longitude)
    }

    @Test
    fun `should create ShopGPS with maximum latitude`() {
        val gps = ShopGPS.of(90.0, 0.0)
        assertEquals(90.0, gps.latitude)
        assertEquals(0.0, gps.longitude)
    }

    @Test
    fun `should create ShopGPS with minimum longitude`() {
        val gps = ShopGPS.of(0.0, -180.0)
        assertEquals(0.0, gps.latitude)
        assertEquals(-180.0, gps.longitude)
    }

    @Test
    fun `should create ShopGPS with maximum longitude`() {
        val gps = ShopGPS.of(0.0, 180.0)
        assertEquals(0.0, gps.latitude)
        assertEquals(180.0, gps.longitude)
    }

    @Test
    fun `should throw InvalidShopGPSException when latitude is less than -90`() {
        assertFailsWith<InvalidShopGPSException> {
            ShopGPS.of(-90.1, 0.0)
        }
    }

    @Test
    fun `should throw InvalidShopGPSException when latitude is greater than 90`() {
        assertFailsWith<InvalidShopGPSException> {
            ShopGPS.of(90.1, 0.0)
        }
    }

    @Test
    fun `should throw InvalidShopGPSException when longitude is less than -180`() {
        assertFailsWith<InvalidShopGPSException> {
            ShopGPS.of(0.0, -180.1)
        }
    }

    @Test
    fun `should throw InvalidShopGPSException when longitude is greater than 180`() {
        assertFailsWith<InvalidShopGPSException> {
            ShopGPS.of(0.0, 180.1)
        }
    }

    @Test
    fun `should create ShopGPS with Seoul coordinates`() {
        val gps = ShopGPS.of(37.5665, 126.9780)
        assertEquals(37.5665, gps.latitude)
        assertEquals(126.9780, gps.longitude)
    }
}
