package com.mad.jellomarkserver.reservation.port.driving

import com.mad.jellomarkserver.reservation.core.domain.model.Reservation

fun interface NoShowReservationUseCase {
    fun execute(command: NoShowReservationCommand): Reservation
}
