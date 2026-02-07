package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.ListMemberReservationsCommand
import com.mad.jellomarkserver.reservation.port.driving.ListMemberReservationsUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ListMemberReservationsUseCaseImpl(
    private val reservationPort: ReservationPort
) : ListMemberReservationsUseCase {

    @Transactional(readOnly = true)
    override fun execute(command: ListMemberReservationsCommand): List<Reservation> {
        val memberId = MemberId.from(UUID.fromString(command.memberId))
        return reservationPort.findByMemberId(memberId)
    }
}
