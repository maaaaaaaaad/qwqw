package com.mad.jellomarkserver.reservation.port.driving

import com.mad.jellomarkserver.reservation.core.domain.model.Reservation

fun interface ListShopReservationsUseCase {
    fun execute(command: ListShopReservationsCommand): List<Reservation>
}
