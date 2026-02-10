package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.AvailableDatesResult
import com.mad.jellomarkserver.reservation.port.driving.GetAvailableDatesQuery
import com.mad.jellomarkserver.reservation.port.driving.GetAvailableDatesUseCase
import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.util.*

@Service
class GetAvailableDatesUseCaseImpl(
    private val beautishopPort: BeautishopPort,
    private val treatmentPort: TreatmentPort,
    private val reservationPort: ReservationPort,
    private val clock: Clock = Clock.systemUTC()
) : GetAvailableDatesUseCase {

    override fun execute(query: GetAvailableDatesQuery): AvailableDatesResult {
        val shopId = ShopId.from(UUID.fromString(query.shopId))
        val treatmentId = TreatmentId.from(UUID.fromString(query.treatmentId))
        val yearMonth = YearMonth.parse(query.yearMonth)

        val shop = beautishopPort.findById(shopId)
            ?: throw BeautishopNotFoundException(query.shopId)

        val treatment = treatmentPort.findById(treatmentId)
            ?: throw TreatmentNotFoundException(query.treatmentId)

        val today = LocalDate.now(clock)
        val startDate = if (yearMonth.atDay(1).isBefore(today)) today else yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()

        if (startDate.isAfter(endDate)) {
            return AvailableDatesResult(emptyList())
        }

        val durationMinutes = treatment.duration.value.toLong()
        val availableDates = mutableListOf<LocalDate>()

        var date = startDate
        while (!date.isAfter(endDate)) {
            val dayOfWeek = date.dayOfWeek.name.lowercase()
            val timeRange = shop.operatingTime.schedule[dayOfWeek]

            if (timeRange != null && timeRange != CLOSED) {
                val (openTimeStr, closeTimeStr) = timeRange.split("-")
                val openTime = LocalTime.parse(openTimeStr)
                val closeTime = LocalTime.parse(closeTimeStr)

                if (hasAvailableSlot(shopId, date, openTime, closeTime, durationMinutes)) {
                    availableDates.add(date)
                }
            }

            date = date.plusDays(1)
        }

        return AvailableDatesResult(availableDates)
    }

    private fun hasAvailableSlot(
        shopId: ShopId,
        date: LocalDate,
        openTime: LocalTime,
        closeTime: LocalTime,
        durationMinutes: Long
    ): Boolean {
        val existingReservations = reservationPort.findByShopIdAndDate(shopId, date)
        val activeReservations = existingReservations.filter {
            it.status == ReservationStatus.PENDING || it.status == ReservationStatus.CONFIRMED
        }

        var candidateStart = openTime
        while (!candidateStart.plusMinutes(durationMinutes).isAfter(closeTime)) {
            val candidateEnd = candidateStart.plusMinutes(durationMinutes)

            val isAvailable = activeReservations.none { reservation ->
                reservation.startTime.isBefore(candidateEnd) && reservation.endTime.isAfter(candidateStart)
            }

            if (isAvailable) return true
            candidateStart = candidateStart.plusMinutes(SLOT_INTERVAL_MINUTES)
        }

        return false
    }

    companion object {
        private const val CLOSED = "closed"
        private const val SLOT_INTERVAL_MINUTES = 30L
    }
}
