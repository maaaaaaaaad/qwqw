package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidOperatingTimeException

data class OperatingTime private constructor(
    val schedule: Map<String, String>
) {
    companion object {
        private val VALID_DAYS = setOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
        private val TIME_RANGE_PATTERN = Regex("^\\d{2}:\\d{2}-\\d{2}:\\d{2}$")
        private const val CLOSED = "closed"

        fun of(schedule: Map<String, String>): OperatingTime {
            try {
                require(schedule.isNotEmpty()) { "Schedule cannot be empty" }

                schedule.forEach { (day, time) ->
                    require(VALID_DAYS.contains(day.lowercase())) { "Invalid day: $day" }

                    if (time != CLOSED) {
                        require(TIME_RANGE_PATTERN.matches(time)) { "Invalid time format: $time" }
                        validateTimeRange(time)
                    }
                }

                return OperatingTime(schedule)
            } catch (ex: IllegalArgumentException) {
                throw InvalidOperatingTimeException(ex.message ?: "Invalid schedule")
            }
        }

        private fun validateTimeRange(timeRange: String) {
            val parts = timeRange.split("-")
            parts.forEach { time ->
                val (hours, minutes) = time.split(":").map { it.toInt() }
                require(hours in 0..23) { "Hours must be between 0 and 23" }
                require(minutes in 0..59) { "Minutes must be between 0 and 59" }
            }
        }
    }
}
