package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.reservation.core.domain.exception.InvalidReservationStatusTransitionException
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.exception.UnauthorizedReservationAccessException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.CompleteReservationCommand
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
class CompleteReservationUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: CompleteReservationUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = CompleteReservationUseCaseImpl(reservationPort, beautishopPort)
    }

    @Test
    fun `should complete confirmed reservation`() {
        val shopId = ShopId.new()
        val ownerId = OwnerId.new()
        val reservation = createTestReservation(shopId = shopId).confirm()
        val shop = createBeautishop(shopId)

        whenever(reservationPort.findById(reservation.id)).thenReturn(reservation)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(reservationPort.save(any())).thenAnswer { it.arguments[0] as Reservation }

        val command = CompleteReservationCommand(
            reservationId = reservation.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        val result = useCase.execute(command)

        assertEquals(ReservationStatus.COMPLETED, result.status)
    }

    @Test
    fun `should throw when completing PENDING reservation`() {
        val shopId = ShopId.new()
        val ownerId = OwnerId.new()
        val reservation = createTestReservation(shopId = shopId)
        val shop = createBeautishop(shopId)

        whenever(reservationPort.findById(reservation.id)).thenReturn(reservation)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = CompleteReservationCommand(
            reservationId = reservation.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        assertFailsWith<InvalidReservationStatusTransitionException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw ReservationNotFoundException when not found`() {
        val reservationId = ReservationId.new()

        whenever(reservationPort.findById(reservationId)).thenReturn(null)

        val command = CompleteReservationCommand(
            reservationId = reservationId.value.toString(),
            ownerId = OwnerId.new().value.toString()
        )

        assertFailsWith<ReservationNotFoundException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw UnauthorizedReservationAccessException when owner does not own shop`() {
        val reservation = createTestReservation()
        val ownerId = OwnerId.new()

        whenever(reservationPort.findById(reservation.id)).thenReturn(reservation)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())

        val command = CompleteReservationCommand(
            reservationId = reservation.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        assertFailsWith<UnauthorizedReservationAccessException> {
            useCase.execute(command)
        }
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

    private fun createBeautishop(shopId: ShopId): Beautishop {
        return Beautishop.reconstruct(
            id = shopId,
            name = ShopName.of("Test Shop"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구"),
            gps = ShopGPS.of(37.5, 127.0),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = null,
            images = ShopImages.empty(),
            averageRating = AverageRating.zero(),
            reviewCount = ReviewCount.zero(),
            createdAt = java.time.Instant.now(),
            updatedAt = java.time.Instant.now()
        )
    }
}
