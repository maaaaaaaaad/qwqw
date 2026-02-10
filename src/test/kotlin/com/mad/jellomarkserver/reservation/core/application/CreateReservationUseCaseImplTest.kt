package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.*
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.reservation.core.domain.exception.PastReservationException
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationTimeConflictException
import com.mad.jellomarkserver.reservation.core.domain.exception.TreatmentNotInShopException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.CreateReservationCommand
import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.model.*
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class CreateReservationUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    @Mock
    private lateinit var treatmentPort: TreatmentPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var memberPort: MemberPort

    @Mock
    private lateinit var sendNotificationUseCase: SendNotificationUseCase

    private lateinit var useCase: CreateReservationUseCaseImpl

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))

    @BeforeEach
    fun setup() {
        useCase = CreateReservationUseCaseImpl(
            reservationPort, treatmentPort, beautishopPort, memberPort, sendNotificationUseCase, fixedClock
        )
    }

    private fun createTreatment(shopId: ShopId, durationMinutes: Int = 60): Treatment {
        return Treatment.reconstruct(
            id = TreatmentId.new(),
            shopId = shopId,
            name = TreatmentName.of("젤네일"),
            price = TreatmentPrice.of(50000),
            duration = TreatmentDuration.of(durationMinutes),
            description = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    @Test
    fun `should create reservation successfully`() {
        val shopId = ShopId.new()
        val treatment = createTreatment(shopId)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.existsOverlapping(any(), any(), any(), any())).thenReturn(false)
        whenever(reservationPort.save(any())).thenAnswer { it.arguments[0] as Reservation }

        val command = CreateReservationCommand(
            shopId = shopId.value.toString(),
            memberId = MemberId.new().value.toString(),
            treatmentId = treatment.id.value.toString(),
            reservationDate = "2025-03-15",
            startTime = "14:00",
            memo = "테스트 메모"
        )

        val result = useCase.execute(command)

        assertNotNull(result.id)
        assertEquals(shopId, result.shopId)
        assertEquals(ReservationStatus.PENDING, result.status)
        assertEquals(LocalDate.of(2025, 3, 15), result.reservationDate)
        assertEquals(LocalTime.of(14, 0), result.startTime)
        assertEquals(LocalTime.of(15, 0), result.endTime)
        assertEquals("테스트 메모", result.memo?.value)
    }

    @Test
    fun `should create reservation without memo`() {
        val shopId = ShopId.new()
        val treatment = createTreatment(shopId)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.existsOverlapping(any(), any(), any(), any())).thenReturn(false)
        whenever(reservationPort.save(any())).thenAnswer { it.arguments[0] as Reservation }

        val command = CreateReservationCommand(
            shopId = shopId.value.toString(),
            memberId = MemberId.new().value.toString(),
            treatmentId = treatment.id.value.toString(),
            reservationDate = "2025-03-15",
            startTime = "14:00",
            memo = null
        )

        val result = useCase.execute(command)

        assertNull(result.memo)
    }

    @Test
    fun `should throw PastReservationException for past date`() {
        val command = CreateReservationCommand(
            shopId = ShopId.new().value.toString(),
            memberId = MemberId.new().value.toString(),
            treatmentId = TreatmentId.new().value.toString(),
            reservationDate = "2024-12-31",
            startTime = "14:00",
            memo = null
        )

        assertFailsWith<PastReservationException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw TreatmentNotFoundException when treatment does not exist`() {
        val treatmentId = TreatmentId.new()

        whenever(treatmentPort.findById(treatmentId)).thenReturn(null)

        val command = CreateReservationCommand(
            shopId = ShopId.new().value.toString(),
            memberId = MemberId.new().value.toString(),
            treatmentId = treatmentId.value.toString(),
            reservationDate = "2025-03-15",
            startTime = "14:00",
            memo = null
        )

        assertFailsWith<TreatmentNotFoundException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw TreatmentNotInShopException when treatment belongs to different shop`() {
        val shopId = ShopId.new()
        val otherShopId = ShopId.new()
        val treatment = createTreatment(otherShopId)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)

        val command = CreateReservationCommand(
            shopId = shopId.value.toString(),
            memberId = MemberId.new().value.toString(),
            treatmentId = treatment.id.value.toString(),
            reservationDate = "2025-03-15",
            startTime = "14:00",
            memo = null
        )

        assertFailsWith<TreatmentNotInShopException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw ReservationTimeConflictException when time overlaps`() {
        val shopId = ShopId.new()
        val treatment = createTreatment(shopId)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.existsOverlapping(any(), any(), any(), any())).thenReturn(true)

        val command = CreateReservationCommand(
            shopId = shopId.value.toString(),
            memberId = MemberId.new().value.toString(),
            treatmentId = treatment.id.value.toString(),
            reservationDate = "2025-03-15",
            startTime = "14:00",
            memo = null
        )

        assertFailsWith<ReservationTimeConflictException> {
            useCase.execute(command)
        }
    }

    @Test
    fun `should calculate endTime based on treatment duration`() {
        val shopId = ShopId.new()
        val treatment = createTreatment(shopId, durationMinutes = 90)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.existsOverlapping(any(), any(), any(), any())).thenReturn(false)
        whenever(reservationPort.save(any())).thenAnswer { it.arguments[0] as Reservation }

        val command = CreateReservationCommand(
            shopId = shopId.value.toString(),
            memberId = MemberId.new().value.toString(),
            treatmentId = treatment.id.value.toString(),
            reservationDate = "2025-03-15",
            startTime = "14:00",
            memo = null
        )

        val result = useCase.execute(command)

        assertEquals(LocalTime.of(15, 30), result.endTime)
    }

    @Test
    fun `should send notification to owner after reservation creation`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val ownerId = OwnerId.new()
        val treatment = createTreatment(shopId)
        val member = Member.reconstruct(
            id = memberId,
            socialProvider = SocialProvider.KAKAO,
            socialId = SocialId("12345"),
            memberNickname = MemberNickname.of("테스트유저"),
            displayName = MemberDisplayName.of("Test User"),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.existsOverlapping(any(), any(), any(), any())).thenReturn(false)
        whenever(reservationPort.save(any())).thenAnswer { it.arguments[0] as Reservation }
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)
        whenever(memberPort.findById(memberId)).thenReturn(member)

        val command = CreateReservationCommand(
            shopId = shopId.value.toString(),
            memberId = memberId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            reservationDate = "2025-03-15",
            startTime = "14:00",
            memo = null
        )

        useCase.execute(command)

        verify(sendNotificationUseCase).execute(argThat<SendNotificationCommand> { cmd ->
            cmd.userId == ownerId.value.toString() &&
                cmd.userRole == "OWNER" &&
                cmd.type == "RESERVATION_CREATED" &&
                cmd.body.contains("테스트유저") &&
                cmd.body.contains("젤네일")
        })
    }

    @Test
    fun `should not fail reservation creation when notification fails`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val treatment = createTreatment(shopId)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.existsOverlapping(any(), any(), any(), any())).thenReturn(false)
        whenever(reservationPort.save(any())).thenAnswer { it.arguments[0] as Reservation }
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenThrow(RuntimeException("DB error"))

        val command = CreateReservationCommand(
            shopId = shopId.value.toString(),
            memberId = memberId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            reservationDate = "2025-03-15",
            startTime = "14:00",
            memo = null
        )

        val result = useCase.execute(command)

        assertNotNull(result.id)
        assertEquals(ReservationStatus.PENDING, result.status)
    }
}
