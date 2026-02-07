package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.GetReservationCommand
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class GetReservationUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    private lateinit var useCase: GetReservationUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = GetReservationUseCaseImpl(reservationPort)
    }

    @Test
    fun `should return reservation when found`() {
        val reservation = createTestReservation()

        whenever(reservationPort.findById(reservation.id)).thenReturn(reservation)

        val command = GetReservationCommand(reservation.id.value.toString())
        val result = useCase.execute(command)

        assertEquals(reservation.id, result.id)
    }

    @Test
    fun `should throw ReservationNotFoundException when not found`() {
        val reservationId = ReservationId.new()

        whenever(reservationPort.findById(reservationId)).thenReturn(null)

        val command = GetReservationCommand(reservationId.value.toString())

        assertFailsWith<ReservationNotFoundException> {
            useCase.execute(command)
        }
    }

    private fun createTestReservation(): Reservation {
        return Reservation.create(
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = null
        )
    }
}
