package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidOperatingTimeException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class OperatingTimeTest {

    @Test
    fun `should create OperatingTime with valid weekly schedule`() {
        val schedule = mapOf(
            "monday" to "09:00-18:00",
            "tuesday" to "09:00-18:00",
            "wednesday" to "09:00-18:00",
            "thursday" to "09:00-18:00",
            "friday" to "09:00-18:00",
            "saturday" to "10:00-15:00",
            "sunday" to "closed"
        )
        val operatingTime = OperatingTime.of(schedule)
        assertEquals(schedule, operatingTime.schedule)
    }

    @Test
    fun `should create OperatingTime with partial weekly schedule`() {
        val schedule = mapOf(
            "monday" to "09:00-18:00",
            "sunday" to "closed"
        )
        val operatingTime = OperatingTime.of(schedule)
        assertEquals(schedule, operatingTime.schedule)
    }

    @Test
    fun `should create OperatingTime with all days closed`() {
        val schedule = mapOf(
            "monday" to "closed",
            "tuesday" to "closed",
            "wednesday" to "closed",
            "thursday" to "closed",
            "friday" to "closed",
            "saturday" to "closed",
            "sunday" to "closed"
        )
        val operatingTime = OperatingTime.of(schedule)
        assertEquals(schedule, operatingTime.schedule)
    }

    @Test
    fun `should create OperatingTime with 24-hour format`() {
        val schedule = mapOf(
            "monday" to "00:00-23:59"
        )
        val operatingTime = OperatingTime.of(schedule)
        assertEquals(schedule, operatingTime.schedule)
    }

    @Test
    fun `should throw InvalidOperatingTimeException when schedule is empty`() {
        assertFailsWith<InvalidOperatingTimeException> {
            OperatingTime.of(emptyMap())
        }
    }

    @Test
    fun `should throw InvalidOperatingTimeException when day has invalid format`() {
        val schedule = mapOf(
            "monday" to "invalid"
        )
        assertFailsWith<InvalidOperatingTimeException> {
            OperatingTime.of(schedule)
        }
    }

    @Test
    fun `should throw InvalidOperatingTimeException when time has invalid format`() {
        val schedule = mapOf(
            "monday" to "25:00-30:00"
        )
        assertFailsWith<InvalidOperatingTimeException> {
            OperatingTime.of(schedule)
        }
    }

    @Test
    fun `should throw InvalidOperatingTimeException when day is invalid`() {
        val schedule = mapOf(
            "invalidday" to "09:00-18:00"
        )
        assertFailsWith<InvalidOperatingTimeException> {
            OperatingTime.of(schedule)
        }
    }
}
