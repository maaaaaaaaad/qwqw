package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.exception.UnauthorizedReservationAccessException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.ConfirmReservationCommand
import com.mad.jellomarkserver.treatment.core.domain.model.*
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class ConfirmReservationUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var treatmentPort: TreatmentPort

    @Mock
    private lateinit var sendNotificationUseCase: SendNotificationUseCase

    private lateinit var useCase: ConfirmReservationUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = ConfirmReservationUseCaseImpl(
            reservationPort, beautishopPort, treatmentPort, sendNotificationUseCase
        )
    }

    @Test
    fun `should confirm reservation successfully`() {
        val shopId = ShopId.new()
        val ownerId = OwnerId.new()
        val reservation = createTestReservation(shopId = shopId)
        val shop = createBeautishop(shopId)

        whenever(reservationPort.findById(reservation.id)).thenReturn(reservation)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(reservationPort.save(any())).thenAnswer { it.arguments[0] as Reservation }

        val command = ConfirmReservationCommand(
            reservationId = reservation.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        val result = useCase.execute(command)

        assertEquals(ReservationStatus.CONFIRMED, result.status)
    }

    @Test
    fun `should throw ReservationNotFoundException when not found`() {
        val reservationId = ReservationId.new()

        whenever(reservationPort.findById(reservationId)).thenReturn(null)

        val command = ConfirmReservationCommand(
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

        val command = ConfirmReservationCommand(
            reservationId = reservation.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        assertFailsWith<UnauthorizedReservationAccessException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should send notification to member after confirmation`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val treatmentId = TreatmentId.new()
        val ownerId = OwnerId.new()
        val reservation = createTestReservation(shopId = shopId, memberId = memberId, treatmentId = treatmentId)
        val shop = createBeautishop(shopId)
        val treatment = createTreatment(treatmentId, shopId)

        whenever(reservationPort.findById(reservation.id)).thenReturn(reservation)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(reservationPort.save(any())).thenAnswer { it.arguments[0] as Reservation }
        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatmentId)).thenReturn(treatment)

        val command = ConfirmReservationCommand(
            reservationId = reservation.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        useCase.execute(command)

        verify(sendNotificationUseCase).execute(argThat<SendNotificationCommand> { cmd ->
            cmd.userId == memberId.value.toString() &&
                cmd.userRole == "MEMBER" &&
                cmd.type == "RESERVATION_CONFIRMED" &&
                cmd.body.contains("Test Shop") &&
                cmd.body.contains("젤네일")
        })
    }

    private fun createTestReservation(
        shopId: ShopId = ShopId.new(),
        memberId: MemberId = MemberId.new(),
        treatmentId: TreatmentId = TreatmentId.new()
    ): Reservation {
        return Reservation.create(
            shopId = shopId,
            memberId = memberId,
            treatmentId = treatmentId,
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
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    private fun createTreatment(treatmentId: TreatmentId, shopId: ShopId): Treatment {
        return Treatment.reconstruct(
            id = treatmentId,
            shopId = shopId,
            name = TreatmentName.of("젤네일"),
            price = TreatmentPrice.of(50000),
            duration = TreatmentDuration.of(60),
            description = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
