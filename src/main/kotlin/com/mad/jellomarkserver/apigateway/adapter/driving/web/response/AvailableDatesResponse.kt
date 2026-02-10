package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.reservation.port.driving.AvailableDatesResult

data class AvailableDatesResponse(
    val availableDates: List<String>
) {
    companion object {
        fun from(result: AvailableDatesResult): AvailableDatesResponse {
            return AvailableDatesResponse(
                availableDates = result.availableDates.map { it.toString() }
            )
        }
    }
}
