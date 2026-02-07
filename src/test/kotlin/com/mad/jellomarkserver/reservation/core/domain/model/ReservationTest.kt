package com.mad.jellomarkserver.reservation.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.exception.InvalidReservationStatusTransitionException
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.test.assertFailsWith

class ReservationTest {

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))
    private val updateClock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneId.of("UTC"))

    @Test
    fun `should create reservation with PENDING status`() {
        val reservation = createTestReservation()

        assertNotNull(reservation.id)
        assertEquals(ReservationStatus.PENDING, reservation.status)
        assertNull(reservation.rejectionReason)
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), reservation.createdAt)
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), reservation.updatedAt)
    }

    @Test
    fun `should confirm PENDING reservation`() {
        val reservation = createTestReservation()

        val confirmed = reservation.confirm(updateClock)

        assertEquals(ReservationStatus.CONFIRMED, confirmed.status)
        assertEquals(reservation.id, confirmed.id)
        assertEquals(Instant.parse("2025-01-02T00:00:00Z"), confirmed.updatedAt)
        assertEquals(reservation.createdAt, confirmed.createdAt)
    }

    @Test
    fun `should reject PENDING reservation with reason`() {
        val reservation = createTestReservation()
        val reason = RejectionReason.of("해당 시간에 예약이 불가합니다")

        val rejected = reservation.reject(reason, updateClock)

        assertEquals(ReservationStatus.REJECTED, rejected.status)
        assertEquals("해당 시간에 예약이 불가합니다", rejected.rejectionReason?.value)
        assertEquals(Instant.parse("2025-01-02T00:00:00Z"), rejected.updatedAt)
    }

    @Test
    fun `should cancel PENDING reservation`() {
        val reservation = createTestReservation()

        val cancelled = reservation.cancel(updateClock)

        assertEquals(ReservationStatus.CANCELLED, cancelled.status)
    }

    @Test
    fun `should cancel CONFIRMED reservation`() {
        val reservation = createTestReservation().confirm(fixedClock)

        val cancelled = reservation.cancel(updateClock)

        assertEquals(ReservationStatus.CANCELLED, cancelled.status)
    }

    @Test
    fun `should complete CONFIRMED reservation`() {
        val reservation = createTestReservation().confirm(fixedClock)

        val completed = reservation.complete(updateClock)

        assertEquals(ReservationStatus.COMPLETED, completed.status)
    }

    @Test
    fun `should mark CONFIRMED reservation as NO_SHOW`() {
        val reservation = createTestReservation().confirm(fixedClock)

        val noShow = reservation.noShow(updateClock)

        assertEquals(ReservationStatus.NO_SHOW, noShow.status)
    }

    @Test
    fun `should throw when confirming non-PENDING reservation`() {
        val reservation = createTestReservation().confirm(fixedClock)

        assertFailsWith<InvalidReservationStatusTransitionException> {
            reservation.confirm(updateClock)
        }
    }

    @Test
    fun `should throw when rejecting CONFIRMED reservation`() {
        val reservation = createTestReservation().confirm(fixedClock)

        assertFailsWith<InvalidReservationStatusTransitionException> {
            reservation.reject(RejectionReason.of("사유"), updateClock)
        }
    }

    @Test
    fun `should throw when completing PENDING reservation`() {
        val reservation = createTestReservation()

        assertFailsWith<InvalidReservationStatusTransitionException> {
            reservation.complete(updateClock)
        }
    }

    @Test
    fun `should throw when cancelling REJECTED reservation`() {
        val reservation = createTestReservation()
            .reject(RejectionReason.of("사유"), fixedClock)

        assertFailsWith<InvalidReservationStatusTransitionException> {
            reservation.cancel(updateClock)
        }
    }

    @Test
    fun `should throw when cancelling COMPLETED reservation`() {
        val reservation = createTestReservation()
            .confirm(fixedClock)
            .complete(fixedClock)

        assertFailsWith<InvalidReservationStatusTransitionException> {
            reservation.cancel(updateClock)
        }
    }

    @Test
    fun `isOwnedByMember should return true for matching memberId`() {
        val memberId = MemberId.new()
        val reservation = createTestReservation(memberId = memberId)

        assertTrue(reservation.isOwnedByMember(memberId))
    }

    @Test
    fun `isOwnedByMember should return false for different memberId`() {
        val reservation = createTestReservation()

        assertFalse(reservation.isOwnedByMember(MemberId.new()))
    }

    @Test
    fun `belongsToShop should return true for matching shopId`() {
        val shopId = ShopId.new()
        val reservation = createTestReservation(shopId = shopId)

        assertTrue(reservation.belongsToShop(shopId))
    }

    @Test
    fun `belongsToShop should return false for different shopId`() {
        val reservation = createTestReservation()

        assertFalse(reservation.belongsToShop(ShopId.new()))
    }

    @Test
    fun `should reconstruct reservation from persisted data`() {
        val id = ReservationId.new()
        val shopId = ShopId.new()
        val memberId = MemberId.new()
        val treatmentId = TreatmentId.new()
        val date = LocalDate.of(2025, 3, 15)
        val startTime = LocalTime.of(14, 0)
        val endTime = LocalTime.of(15, 0)
        val memo = ReservationMemo.of("메모")
        val reason = RejectionReason.of("사유")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-02T00:00:00Z")

        val reservation = Reservation.reconstruct(
            id = id,
            shopId = shopId,
            memberId = memberId,
            treatmentId = treatmentId,
            reservationDate = date,
            startTime = startTime,
            endTime = endTime,
            status = ReservationStatus.REJECTED,
            memo = memo,
            rejectionReason = reason,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, reservation.id)
        assertEquals(shopId, reservation.shopId)
        assertEquals(memberId, reservation.memberId)
        assertEquals(treatmentId, reservation.treatmentId)
        assertEquals(date, reservation.reservationDate)
        assertEquals(startTime, reservation.startTime)
        assertEquals(endTime, reservation.endTime)
        assertEquals(ReservationStatus.REJECTED, reservation.status)
        assertEquals("메모", reservation.memo?.value)
        assertEquals("사유", reservation.rejectionReason?.value)
        assertEquals(createdAt, reservation.createdAt)
        assertEquals(updatedAt, reservation.updatedAt)
    }

    @Test
    fun `should preserve memo through state transitions`() {
        val memo = ReservationMemo.of("테스트 메모")
        val reservation = createTestReservation(memo = memo)

        val confirmed = reservation.confirm(updateClock)

        assertEquals("테스트 메모", confirmed.memo?.value)
    }

    private fun createTestReservation(
        shopId: ShopId = ShopId.new(),
        memberId: MemberId = MemberId.new(),
        memo: ReservationMemo? = null
    ): Reservation {
        return Reservation.create(
            shopId = shopId,
            memberId = memberId,
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = memo,
            clock = fixedClock
        )
    }
}
