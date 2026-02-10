package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.reservation.core.domain.model.AvailableSlot
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.AvailableSlotsResult
import com.mad.jellomarkserver.reservation.port.driving.GetAvailableSlotsQuery
import com.mad.jellomarkserver.reservation.port.driving.GetAvailableSlotsUseCase
import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Service
class GetAvailableSlotsUseCaseImpl(
    private val beautishopPort: BeautishopPort,
    private val treatmentPort: TreatmentPort,
    private val reservationPort: ReservationPort,
    private val clock: Clock = Clock.systemUTC()
) : GetAvailableSlotsUseCase {

    override fun execute(query: GetAvailableSlotsQuery): AvailableSlotsResult {
        val shopId = ShopId.from(UUID.fromString(query.shopId))
        val treatmentId = TreatmentId.from(UUID.fromString(query.treatmentId))
        val date = LocalDate.parse(query.date)

        val shop = beautishopPort.findById(shopId)
            ?: throw BeautishopNotFoundException(query.shopId)

        val treatment = treatmentPort.findById(treatmentId)
            ?: throw TreatmentNotFoundException(query.treatmentId)

        val dayOfWeek = date.dayOfWeek.name.lowercase()
        val timeRange = shop.operatingTime.schedule[dayOfWeek]

        if (timeRange == null || timeRange == CLOSED) {
            return AvailableSlotsResult(
                date = date,
                openTime = LocalTime.MIN,
                closeTime = LocalTime.MIN,
                slots = emptyList()
            )
        }

        val (openTimeStr, closeTimeStr) = timeRange.split("-")
        val openTime = LocalTime.parse(openTimeStr)
        val closeTime = LocalTime.parse(closeTimeStr)
        val durationMinutes = treatment.duration.value.toLong()

        val existingReservations = reservationPort.findByShopIdAndDate(shopId, date)
        val activeReservations = existingReservations.filter {
            it.status == ReservationStatus.PENDING || it.status == ReservationStatus.CONFIRMED
        }

        val slots = mutableListOf<AvailableSlot>()
        var candidateStart = openTime

        while (!candidateStart.plusMinutes(durationMinutes).isAfter(closeTime)) {
            val candidateEnd = candidateStart.plusMinutes(durationMinutes)

            val isAvailable = activeReservations.none { reservation ->
                reservation.startTime.isBefore(candidateEnd) && reservation.endTime.isAfter(candidateStart)
            }

            slots.add(AvailableSlot(startTime = candidateStart, available = isAvailable))
            candidateStart = candidateStart.plusMinutes(SLOT_INTERVAL_MINUTES)
        }

        return AvailableSlotsResult(
            date = date,
            openTime = openTime,
            closeTime = closeTime,
            slots = slots
        )
    }

    companion object {
        private const val CLOSED = "closed"
        private const val SLOT_INTERVAL_MINUTES = 30L
    }
}
