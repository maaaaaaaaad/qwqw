package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.*
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.reservation.core.domain.exception.ReservationNotFoundException
import com.mad.jellomarkserver.reservation.core.domain.exception.UnauthorizedReservationAccessException
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.CancelReservationCommand
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
class CancelReservationUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var treatmentPort: TreatmentPort

    @Mock
    private lateinit var memberPort: MemberPort

    @Mock
    private lateinit var sendNotificationUseCase: SendNotificationUseCase

    private lateinit var useCase: CancelReservationUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = CancelReservationUseCaseImpl(
            reservationPort, beautishopPort, treatmentPort, memberPort, sendNotificationUseCase
        )
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

    @Test
    fun `should send notification to owner after cancellation`() {
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val treatmentId = TreatmentId.new()
        val ownerId = OwnerId.new()
        val reservation = createTestReservation(shopId = shopId, memberId = memberId, treatmentId = treatmentId)
        val member = Member.reconstruct(
            id = memberId,
            socialProvider = SocialProvider.KAKAO,
            socialId = SocialId("12345"),
            memberNickname = MemberNickname.of("테스트유저"),
            displayName = MemberDisplayName.of("Test User"),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val treatment = createTreatment(treatmentId, shopId)

        whenever(reservationPort.findById(reservation.id)).thenReturn(reservation)
        whenever(reservationPort.save(any())).thenAnswer { it.arguments[0] as Reservation }
        whenever(beautishopPort.findOwnerIdByShopId(shopId)).thenReturn(ownerId)
        whenever(memberPort.findById(memberId)).thenReturn(member)
        whenever(treatmentPort.findById(treatmentId)).thenReturn(treatment)

        val command = CancelReservationCommand(
            reservationId = reservation.id.value.toString(),
            memberId = memberId.value.toString()
        )

        useCase.execute(command)

        verify(sendNotificationUseCase).execute(argThat<SendNotificationCommand> { cmd ->
            cmd.userId == ownerId.value.toString() &&
                cmd.userRole == "OWNER" &&
                cmd.type == "RESERVATION_CANCELLED" &&
                cmd.body.contains("테스트유저") &&
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
