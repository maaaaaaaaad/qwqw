package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.GetReservationCommand
import com.mad.jellomarkserver.reservation.port.driving.GetReservationUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class GetReservationUseCaseImpl(
    private val reservationPort: ReservationPort
) : GetReservationUseCase {

    @Transactional(readOnly = true)
    override fun execute(command: GetReservationCommand): Reservation {
        val reservationId = ReservationId.from(UUID.fromString(command.reservationId))
        return reservationPort.findById(reservationId)
            ?: throw ReservationNotFoundException(command.reservationId)
    }
}
