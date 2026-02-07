package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.ListMemberReservationsCommand
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

@ExtendWith(MockitoExtension::class)
class ListMemberReservationsUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    private lateinit var useCase: ListMemberReservationsUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = ListMemberReservationsUseCaseImpl(reservationPort)
    }

    @Test
    fun `should return reservations for member`() {
        val memberId = MemberId.new()
        val reservations = listOf(
            createTestReservation(memberId = memberId),
            createTestReservation(memberId = memberId)
        )

        whenever(reservationPort.findByMemberId(memberId)).thenReturn(reservations)

        val command = ListMemberReservationsCommand(memberId.value.toString())
        val result = useCase.execute(command)

        assertEquals(2, result.size)
    }

    @Test
    fun `should return empty list when no reservations`() {
        val memberId = MemberId.new()

        whenever(reservationPort.findByMemberId(memberId)).thenReturn(emptyList())

        val command = ListMemberReservationsCommand(memberId.value.toString())
        val result = useCase.execute(command)

        assertEquals(0, result.size)
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
