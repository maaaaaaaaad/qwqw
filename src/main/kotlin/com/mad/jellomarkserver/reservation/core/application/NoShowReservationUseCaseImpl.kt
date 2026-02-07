package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.exception.UnauthorizedReservationAccessException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.NoShowReservationCommand
import com.mad.jellomarkserver.reservation.port.driving.NoShowReservationUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class NoShowReservationUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val beautishopPort: BeautishopPort
) : NoShowReservationUseCase {

    @Transactional
    override fun execute(command: NoShowReservationCommand): Reservation {
        val reservationId = ReservationId.from(UUID.fromString(command.reservationId))
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))

        val reservation = reservationPort.findById(reservationId)
            ?: throw ReservationNotFoundException(command.reservationId)

        validateOwnerAccess(ownerId, reservation)

        val noShow = reservation.noShow()
        return reservationPort.save(noShow)
    }

    private fun validateOwnerAccess(ownerId: OwnerId, reservation: Reservation) {
        val ownerShops = beautishopPort.findByOwnerId(ownerId)
        val ownsShop = ownerShops.any { it.id == reservation.shopId }
        if (!ownsShop) {
            throw UnauthorizedReservationAccessException(
                reservation.id.value.toString(), ownerId.value.toString()
            )
        }
    }
}
