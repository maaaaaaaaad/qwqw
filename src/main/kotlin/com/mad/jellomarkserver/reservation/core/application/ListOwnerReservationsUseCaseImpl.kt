package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.ListOwnerReservationsCommand
import com.mad.jellomarkserver.reservation.port.driving.ListOwnerReservationsUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListOwnerReservationsUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val beautishopPort: BeautishopPort,
    private val ownerPort: OwnerPort
) : ListOwnerReservationsUseCase {

    @Transactional(readOnly = true)
    override fun execute(command: ListOwnerReservationsCommand): List<Reservation> {
        val owner = ownerPort.findByEmail(OwnerEmail.of(command.ownerEmail))
            ?: throw OwnerNotFoundException(command.ownerEmail)

        val shops = beautishopPort.findByOwnerId(owner.id)
        if (shops.isEmpty()) return emptyList()

        val shopIds = shops.map { it.id }
        return reservationPort.findByShopIds(shopIds)
    }
}
