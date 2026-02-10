package com.mad.jellomarkserver.reservation.core.domain.model

import java.time.LocalTime

data class AvailableSlot(
    val startTime: LocalTime,
    val available: Boolean
)
