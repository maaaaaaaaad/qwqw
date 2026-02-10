package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.reservation.port.driving.AvailableSlotsResult

data class AvailableSlotsResponse(
    val date: String,
    val openTime: String,
    val closeTime: String,
    val slots: List<SlotResponse>
) {
    data class SlotResponse(
        val startTime: String,
        val available: Boolean
    )

    companion object {
        fun from(result: AvailableSlotsResult): AvailableSlotsResponse {
            return AvailableSlotsResponse(
                date = result.date.toString(),
                openTime = result.openTime.toString(),
                closeTime = result.closeTime.toString(),
                slots = result.slots.map { slot ->
                    SlotResponse(
                        startTime = slot.startTime.toString(),
                        available = slot.available
                    )
                }
            )
        }
    }
}
