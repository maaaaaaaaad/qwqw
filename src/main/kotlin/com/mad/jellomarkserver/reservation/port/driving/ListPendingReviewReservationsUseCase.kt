package com.mad.jellomarkserver.reservation.port.driving

import com.mad.jellomarkserver.reservation.core.domain.model.Reservation

fun interface ListPendingReviewReservationsUseCase {
    fun execute(command: ListPendingReviewReservationsCommand): List<Reservation>
}
