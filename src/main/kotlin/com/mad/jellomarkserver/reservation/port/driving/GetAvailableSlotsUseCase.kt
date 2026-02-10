package com.mad.jellomarkserver.reservation.port.driving

import com.mad.jellomarkserver.reservation.core.domain.model.AvailableSlot
import java.time.LocalDate
import java.time.LocalTime

data class AvailableSlotsResult(
    val date: LocalDate,
    val openTime: LocalTime,
    val closeTime: LocalTime,
    val slots: List<AvailableSlot>
)

fun interface GetAvailableSlotsUseCase {
    fun execute(query: GetAvailableSlotsQuery): AvailableSlotsResult
}
