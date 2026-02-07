package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.exception.UnauthorizedReservationAccessException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.CancelReservationCommand
import com.mad.jellomarkserver.reservation.port.driving.CancelReservationUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CancelReservationUseCaseImpl(
    private val reservationPort: ReservationPort
) : CancelReservationUseCase {

    @Transactional
    override fun execute(command: CancelReservationCommand): Reservation {
        val reservationId = ReservationId.from(UUID.fromString(command.reservationId))
        val memberId = MemberId.from(UUID.fromString(command.memberId))

        val reservation = reservationPort.findById(reservationId)
            ?: throw ReservationNotFoundException(command.reservationId)

        if (!reservation.isOwnedByMember(memberId)) {
            throw UnauthorizedReservationAccessException(
                command.reservationId, command.memberId
            )
        }

        val cancelled = reservation.cancel()
        return reservationPort.save(cancelled)
    }
}
