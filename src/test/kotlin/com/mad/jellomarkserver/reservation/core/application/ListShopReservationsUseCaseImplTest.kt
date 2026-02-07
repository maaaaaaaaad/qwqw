package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.ListShopReservationsCommand
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
class ListShopReservationsUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    private lateinit var useCase: ListShopReservationsUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = ListShopReservationsUseCaseImpl(reservationPort)
    }

    @Test
    fun `should return reservations for shop`() {
        val shopId = ShopId.new()
        val reservations = listOf(
            createTestReservation(shopId = shopId),
            createTestReservation(shopId = shopId)
        )

        whenever(reservationPort.findByShopId(shopId)).thenReturn(reservations)

        val command = ListShopReservationsCommand(shopId.value.toString())
        val result = useCase.execute(command)

        assertEquals(2, result.size)
    }

    @Test
    fun `should return empty list when no reservations`() {
        val shopId = ShopId.new()

        whenever(reservationPort.findByShopId(shopId)).thenReturn(emptyList())

        val command = ListShopReservationsCommand(shopId.value.toString())
        val result = useCase.execute(command)

        assertEquals(0, result.size)
    }

    private fun createTestReservation(shopId: ShopId = ShopId.new()): Reservation {
        return Reservation.create(
            shopId = shopId,
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = null
        )
    }
}
