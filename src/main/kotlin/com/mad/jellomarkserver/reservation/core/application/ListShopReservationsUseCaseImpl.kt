package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.ListShopReservationsCommand
import com.mad.jellomarkserver.reservation.port.driving.ListShopReservationsUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ListShopReservationsUseCaseImpl(
    private val reservationPort: ReservationPort
) : ListShopReservationsUseCase {

    @Transactional(readOnly = true)
    override fun execute(command: ListShopReservationsCommand): List<Reservation> {
        val shopId = ShopId.from(UUID.fromString(command.shopId))
        return reservationPort.findByShopId(shopId)
    }
}
