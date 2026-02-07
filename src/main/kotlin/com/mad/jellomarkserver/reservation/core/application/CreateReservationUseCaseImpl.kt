package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.exception.PastReservationException
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationTimeConflictException
import com.mad.jellomarkserver.reservation.core.domain.exception.TreatmentNotInShopException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationMemo
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.CreateReservationCommand
import com.mad.jellomarkserver.reservation.port.driving.CreateReservationUseCase
import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Service
class CreateReservationUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val treatmentPort: TreatmentPort,
    private val clock: Clock = Clock.systemUTC()
) : CreateReservationUseCase {

    @Transactional
    override fun execute(command: CreateReservationCommand): Reservation {
        val shopId = ShopId.from(UUID.fromString(command.shopId))
        val memberId = MemberId.from(UUID.fromString(command.memberId))
        val treatmentId = TreatmentId.from(UUID.fromString(command.treatmentId))
        val reservationDate = LocalDate.parse(command.reservationDate)
        val startTime = LocalTime.parse(command.startTime)

        if (reservationDate.isBefore(LocalDate.now(clock))) {
            throw PastReservationException(command.reservationDate)
        }

        val treatment = treatmentPort.findById(treatmentId)
            ?: throw TreatmentNotFoundException(command.treatmentId)

        if (treatment.shopId != shopId) {
            throw TreatmentNotInShopException(command.treatmentId, command.shopId)
        }

        val endTime = startTime.plusMinutes(treatment.duration.value.toLong())

        if (reservationPort.existsOverlapping(shopId, reservationDate, startTime, endTime)) {
            throw ReservationTimeConflictException(
                command.shopId, command.reservationDate, startTime.toString(), endTime.toString()
            )
        }

        val reservation = Reservation.create(
            shopId = shopId,
            memberId = memberId,
            treatmentId = treatmentId,
            reservationDate = reservationDate,
            startTime = startTime,
            endTime = endTime,
            memo = ReservationMemo.ofNullable(command.memo),
            clock = clock
        )

        return reservationPort.save(reservation)
    }
}
