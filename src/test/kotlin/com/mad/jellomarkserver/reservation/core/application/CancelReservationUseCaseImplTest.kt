package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.exception.UnauthorizedReservationAccessException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.CancelReservationCommand
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class CancelReservationUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    private lateinit var useCase: CancelReservationUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = CancelReservationUseCaseImpl(reservationPort)
    }

    @Test
    fun `should cancel reservation by member`() {
        val memberId = MemberId.new()
        val reservation = createTestReservation(memberId = memberId)

        whenever(reservationPort.findById(reservation.id)).thenReturn(reservation)
        whenever(reservationPort.save(any())).thenAnswer { it.arguments[0] as Reservation }

        val command = CancelReservationCommand(
            reservationId = reservation.id.value.toString(),
            memberId = memberId.value.toString()
        )

        val result = useCase.execute(command)

        assertEquals(ReservationStatus.CANCELLED, result.status)
    }

    @Test
    fun `should throw ReservationNotFoundException when not found`() {
        val reservationId = ReservationId.new()

        whenever(reservationPort.findById(reservationId)).thenReturn(null)

        val command = CancelReservationCommand(
            reservationId = reservationId.value.toString(),
            memberId = MemberId.new().value.toString()
        )

        assertFailsWith<ReservationNotFoundException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw UnauthorizedReservationAccessException when member does not own reservation`() {
        val reservation = createTestReservation()
        val otherMemberId = MemberId.new()

        whenever(reservationPort.findById(reservation.id)).thenReturn(reservation)

        val command = CancelReservationCommand(
            reservationId = reservation.id.value.toString(),
            memberId = otherMemberId.value.toString()
        )

        assertFailsWith<UnauthorizedReservationAccessException> {
            useCase.execute(command)
        }
    }

    private fun createTestReservation(memberId: MemberId = MemberId.new()): Reservation {
        return Reservation.create(
            shopId = ShopId.new(),
            memberId = memberId,
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = null
        )
    }
}
