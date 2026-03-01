package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.*

@ExtendWith(MockitoExtension::class)
class NotifyUnprocessedReservationsUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var sendNotificationUseCase: SendNotificationUseCase

    private lateinit var useCase: NotifyUnprocessedReservationsUseCaseImpl

    private val kstZone = ZoneId.of("Asia/Seoul")
    private val fixedInstant = LocalDateTime.of(2025, 6, 15, 19, 0)
        .atZone(kstZone).toInstant()
    private val fixedClock = Clock.fixed(fixedInstant, kstZone)

    @BeforeEach
    fun setup() {
        useCase = NotifyUnprocessedReservationsUseCaseImpl(
            reservationPort, beautishopPort, sendNotificationUseCase, fixedClock
        )
    }

    @Test
    fun `should send notification when shop has unprocessed reservations after closing time`() {
        val shopId = ShopId.new()
        val ownerId = OwnerId.new()
        val reservation = createConfirmedReservation(shopId)
        val shop = createBeautishop(shopId, mapOf("sunday" to "09:00-18:00"))

        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(listOf(reservation))
        whenever(beautishopPort.findByIds(listOf(shopId))).thenReturn(listOf(shop))
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)

        useCase.execute()

        verify(sendNotificationUseCase).execute(argThat<SendNotificationCommand> { cmd ->
            cmd.userId == ownerId.value.toString() &&
                cmd.userRole == "OWNER" &&
                cmd.type == "UNPROCESSED_RESERVATION_REMINDER"
        })
    }

    @Test
    fun `should not send notification when shop is still open`() {
        val shopId = ShopId.new()
        val reservation = createConfirmedReservation(shopId)
        val shop = createBeautishop(shopId, mapOf("sunday" to "09:00-20:00"))

        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(listOf(reservation))
        whenever(beautishopPort.findByIds(listOf(shopId))).thenReturn(listOf(shop))

        useCase.execute()

        verify(sendNotificationUseCase, never()).execute(any())
    }

    @Test
    fun `should not send notification when shop is closed today`() {
        val shopId = ShopId.new()
        val reservation = createConfirmedReservation(shopId)
        val shop = createBeautishop(shopId, mapOf("sunday" to "closed"))

        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(listOf(reservation))
        whenever(beautishopPort.findByIds(listOf(shopId))).thenReturn(listOf(shop))

        useCase.execute()

        verify(sendNotificationUseCase, never()).execute(any())
    }

    @Test
    fun `should not send notification when no unprocessed reservations exist`() {
        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(emptyList())

        useCase.execute()

        verify(sendNotificationUseCase, never()).execute(any())
    }

    @Test
    fun `should send notification only once per shop even with multiple reservations`() {
        val shopId = ShopId.new()
        val ownerId = OwnerId.new()
        val reservation1 = createConfirmedReservation(shopId)
        val reservation2 = createConfirmedReservation(shopId)
        val shop = createBeautishop(shopId, mapOf("sunday" to "09:00-18:00"))

        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(listOf(reservation1, reservation2))
        whenever(beautishopPort.findByIds(listOf(shopId))).thenReturn(listOf(shop))
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)

        useCase.execute()

        verify(sendNotificationUseCase, times(1)).execute(any())
    }

    @Test
    fun `should continue processing other shops when one shop fails`() {
        val shopId1 = ShopId.new()
        val shopId2 = ShopId.new()
        val ownerId1 = OwnerId.new()
        val ownerId2 = OwnerId.new()
        val reservation1 = createConfirmedReservation(shopId1)
        val reservation2 = createConfirmedReservation(shopId2)
        val shop1 = createBeautishop(shopId1, mapOf("sunday" to "09:00-18:00"))
        val shop2 = createBeautishop(shopId2, mapOf("sunday" to "09:00-18:00"))

        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(listOf(reservation1, reservation2))
        whenever(beautishopPort.findByIds(argThat { size == 2 }))
            .thenReturn(listOf(shop1, shop2))
        whenever(beautishopPort.findOwnerIdByShopId(shopId1)).thenReturn(ownerId1)
        whenever(beautishopPort.findOwnerIdByShopId(shopId2)).thenReturn(ownerId2)
        whenever(sendNotificationUseCase.execute(argThat<SendNotificationCommand> {
            userId == ownerId1.value.toString()
        })).thenThrow(RuntimeException("FCM error"))

        useCase.execute()

        verify(sendNotificationUseCase, times(2)).execute(any())
    }

    @Test
    fun `should skip shop when owner not found`() {
        val shopId = ShopId.new()
        val reservation = createConfirmedReservation(shopId)
        val shop = createBeautishop(shopId, mapOf("sunday" to "09:00-18:00"))

        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(listOf(reservation))
        whenever(beautishopPort.findByIds(listOf(shopId))).thenReturn(listOf(shop))
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(null)

        useCase.execute()

        verify(sendNotificationUseCase, never()).execute(any())
    }

    @Test
    fun `should skip shop when shop not found in batch load`() {
        val shopId = ShopId.new()
        val reservation = createConfirmedReservation(shopId)

        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(listOf(reservation))
        whenever(beautishopPort.findByIds(listOf(shopId))).thenReturn(emptyList())

        useCase.execute()

        verify(sendNotificationUseCase, never()).execute(any())
    }

    @Test
    fun `should not send duplicate notification for same shop on second execution`() {
        val shopId = ShopId.new()
        val ownerId = OwnerId.new()
        val reservation = createConfirmedReservation(shopId)
        val shop = createBeautishop(shopId, mapOf("sunday" to "09:00-18:00"))

        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(listOf(reservation))
        whenever(beautishopPort.findByIds(listOf(shopId))).thenReturn(listOf(shop))
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)

        useCase.execute()
        useCase.execute()

        verify(sendNotificationUseCase, times(1)).execute(any())
    }

    @Test
    fun `should include unprocessed count in notification body`() {
        val shopId = ShopId.new()
        val ownerId = OwnerId.new()
        val reservation1 = createConfirmedReservation(shopId)
        val reservation2 = createConfirmedReservation(shopId)
        val shop = createBeautishop(shopId, mapOf("sunday" to "09:00-18:00"))

        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(listOf(reservation1, reservation2))
        whenever(beautishopPort.findByIds(listOf(shopId))).thenReturn(listOf(shop))
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)

        useCase.execute()

        verify(sendNotificationUseCase).execute(argThat<SendNotificationCommand> { cmd ->
            cmd.body.contains("2")
        })
    }

    @Test
    fun `should handle shop with no schedule for today`() {
        val shopId = ShopId.new()
        val reservation = createConfirmedReservation(shopId)
        val shop = createBeautishop(shopId, mapOf("monday" to "09:00-18:00"))

        whenever(reservationPort.findByStatusAndDate(eq(ReservationStatus.CONFIRMED), any()))
            .thenReturn(listOf(reservation))
        whenever(beautishopPort.findByIds(listOf(shopId))).thenReturn(listOf(shop))

        useCase.execute()

        verify(sendNotificationUseCase, never()).execute(any())
    }

    private fun createConfirmedReservation(shopId: ShopId): Reservation {
        return Reservation.create(
            shopId = shopId,
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 6, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = null
        ).confirm()
    }

    private fun createBeautishop(shopId: ShopId, schedule: Map<String, String>): Beautishop {
        return Beautishop.reconstruct(
            id = shopId,
            name = ShopName.of("Test Shop"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구"),
            gps = ShopGPS.of(37.5, 127.0),
            operatingTime = OperatingTime.of(schedule),
            description = null,
            images = ShopImages.empty(),
            averageRating = AverageRating.zero(),
            reviewCount = ReviewCount.zero(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
