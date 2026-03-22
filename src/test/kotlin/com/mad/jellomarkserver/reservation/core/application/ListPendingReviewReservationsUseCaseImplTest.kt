package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.ListPendingReviewReservationsCommand
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime

@ExtendWith(MockitoExtension::class)
class ListPendingReviewReservationsUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    @Mock
    private lateinit var shopReviewPort: ShopReviewPort

    @Mock
    private lateinit var memberPort: MemberPort

    private lateinit var useCase: ListPendingReviewReservationsUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = ListPendingReviewReservationsUseCaseImpl(reservationPort, shopReviewPort, memberPort)
    }

    @Test
    fun `should return completed reservations without reviews`() {
        val memberId = MemberId.new()
        val socialId = "member@test.com"
        val member = createTestMember(memberId, socialId)
        val completedRes1 = createCompletedReservation(memberId, ShopId.new())
        val completedRes2 = createCompletedReservation(memberId, ShopId.new())
        val completedRes3 = createCompletedReservation(memberId, ShopId.new())
        val pendingRes = createTestReservation(memberId, ShopId.new())

        whenever(memberPort.findBySocialId(SocialId(socialId))).thenReturn(member)
        whenever(reservationPort.findByMemberId(memberId)).thenReturn(
            listOf(completedRes1, completedRes2, completedRes3, pendingRes)
        )
        whenever(shopReviewPort.findReviewedReservationIdsByMemberId(memberId))
            .thenReturn(setOf(completedRes1.id))

        val command = ListPendingReviewReservationsCommand(socialId)
        val result = useCase.execute(command)

        assertEquals(2, result.size)
        assertTrue(result.all { it.status == ReservationStatus.COMPLETED })
    }

    @Test
    fun `should return empty when all completed reservations have reviews`() {
        val memberId = MemberId.new()
        val socialId = "member@test.com"
        val member = createTestMember(memberId, socialId)
        val completedRes = createCompletedReservation(memberId, ShopId.new())

        whenever(memberPort.findBySocialId(SocialId(socialId))).thenReturn(member)
        whenever(reservationPort.findByMemberId(memberId)).thenReturn(listOf(completedRes))
        whenever(shopReviewPort.findReviewedReservationIdsByMemberId(memberId))
            .thenReturn(setOf(completedRes.id))

        val command = ListPendingReviewReservationsCommand(socialId)
        val result = useCase.execute(command)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty when no completed reservations`() {
        val memberId = MemberId.new()
        val socialId = "member@test.com"
        val member = createTestMember(memberId, socialId)
        val pendingRes = createTestReservation(memberId, ShopId.new())

        whenever(memberPort.findBySocialId(SocialId(socialId))).thenReturn(member)
        whenever(reservationPort.findByMemberId(memberId)).thenReturn(listOf(pendingRes))

        val command = ListPendingReviewReservationsCommand(socialId)
        val result = useCase.execute(command)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should throw when member not found`() {
        val socialId = "unknown@test.com"

        whenever(memberPort.findBySocialId(SocialId(socialId))).thenReturn(null)

        val command = ListPendingReviewReservationsCommand(socialId)

        assertThrows<MemberNotFoundException> {
            useCase.execute(command)
        }
    }

    private fun createTestReservation(memberId: MemberId, shopId: ShopId): Reservation {
        return Reservation.create(
            shopId = shopId,
            memberId = memberId,
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = null
        )
    }

    private fun createCompletedReservation(memberId: MemberId, shopId: ShopId): Reservation {
        val reservation = createTestReservation(memberId, shopId)
        val confirmed = reservation.confirm()
        return confirmed.complete()
    }

    private fun createTestMember(
        memberId: MemberId,
        socialId: String
    ): com.mad.jellomarkserver.member.core.domain.model.Member {
        return com.mad.jellomarkserver.member.core.domain.model.Member.reconstruct(
            id = memberId,
            socialProvider = com.mad.jellomarkserver.member.core.domain.model.SocialProvider.KAKAO,
            socialId = SocialId(socialId),
            memberNickname = com.mad.jellomarkserver.member.core.domain.model.MemberNickname.of("TestMember"),
            displayName = com.mad.jellomarkserver.member.core.domain.model.MemberDisplayName("TestDisplay"),
            createdAt = java.time.Instant.now(),
            updatedAt = java.time.Instant.now()
        )
    }
}
