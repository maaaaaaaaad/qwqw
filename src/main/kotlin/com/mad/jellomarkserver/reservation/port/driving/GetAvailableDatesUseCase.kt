package com.mad.jellomarkserver.reservation.port.driving

import java.time.LocalDate

data class AvailableDatesResult(
    val availableDates: List<LocalDate>
)

fun interface GetAvailableDatesUseCase {
    fun execute(query: GetAvailableDatesQuery): AvailableDatesResult
}
