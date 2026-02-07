package com.mad.jellomarkserver.reservation.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.adapter.driven.persistence.entity.ReservationJpaEntity
import com.mad.jellomarkserver.reservation.core.domain.model.*
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class ReservationMapperImplTest {

    private val mapper = ReservationMapperImpl()

    @Test
    fun `should map domain to entity`() {
        val reservation = Reservation.reconstruct(
            id = ReservationId.from(UUID.randomUUID()),
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            status = ReservationStatus.PENDING,
            memo = ReservationMemo.of("메모"),
            rejectionReason = null,
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        )

        val entity = mapper.toEntity(reservation)

        assertEquals(reservation.id.value, entity.id)
        assertEquals(reservation.shopId.value, entity.shopId)
        assertEquals(reservation.memberId.value, entity.memberId)
        assertEquals(reservation.treatmentId.value, entity.treatmentId)
        assertEquals(reservation.reservationDate, entity.reservationDate)
        assertEquals(reservation.startTime, entity.startTime)
        assertEquals(reservation.endTime, entity.endTime)
        assertEquals("PENDING", entity.status)
        assertEquals("메모", entity.memo)
        assertNull(entity.rejectionReason)
        assertEquals(reservation.createdAt, entity.createdAt)
        assertEquals(reservation.updatedAt, entity.updatedAt)
    }

    @Test
    fun `should map entity to domain`() {
        val id = UUID.randomUUID()
        val shopId = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        val treatmentId = UUID.randomUUID()
        val now = Instant.now()

        val entity = ReservationJpaEntity(
            id = id,
            shopId = shopId,
            memberId = memberId,
            treatmentId = treatmentId,
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            status = "CONFIRMED",
            memo = "테스트 메모",
            rejectionReason = null,
            createdAt = now,
            updatedAt = now
        )

        val domain = mapper.toDomain(entity)

        assertEquals(id, domain.id.value)
        assertEquals(shopId, domain.shopId.value)
        assertEquals(memberId, domain.memberId.value)
        assertEquals(treatmentId, domain.treatmentId.value)
        assertEquals(LocalDate.of(2025, 3, 15), domain.reservationDate)
        assertEquals(LocalTime.of(14, 0), domain.startTime)
        assertEquals(LocalTime.of(15, 0), domain.endTime)
        assertEquals(ReservationStatus.CONFIRMED, domain.status)
        assertEquals("테스트 메모", domain.memo?.value)
        assertNull(domain.rejectionReason)
    }

    @Test
    fun `should map entity with rejection reason to domain`() {
        val entity = ReservationJpaEntity(
            id = UUID.randomUUID(),
            shopId = UUID.randomUUID(),
            memberId = UUID.randomUUID(),
            treatmentId = UUID.randomUUID(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            status = "REJECTED",
            memo = null,
            rejectionReason = "예약 불가",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val domain = mapper.toDomain(entity)

        assertEquals(ReservationStatus.REJECTED, domain.status)
        assertNull(domain.memo)
        assertEquals("예약 불가", domain.rejectionReason?.value)
    }

    @Test
    fun `should roundtrip domain to entity and back`() {
        val original = Reservation.reconstruct(
            id = ReservationId.from(UUID.randomUUID()),
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            status = ReservationStatus.REJECTED,
            memo = ReservationMemo.of("메모"),
            rejectionReason = RejectionReason.of("거절 사유"),
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-02T00:00:00Z")
        )

        val entity = mapper.toEntity(original)
        val restored = mapper.toDomain(entity)

        assertEquals(original.id, restored.id)
        assertEquals(original.shopId, restored.shopId)
        assertEquals(original.memberId, restored.memberId)
        assertEquals(original.treatmentId, restored.treatmentId)
        assertEquals(original.reservationDate, restored.reservationDate)
        assertEquals(original.startTime, restored.startTime)
        assertEquals(original.endTime, restored.endTime)
        assertEquals(original.status, restored.status)
        assertEquals(original.memo?.value, restored.memo?.value)
        assertEquals(original.rejectionReason?.value, restored.rejectionReason?.value)
        assertEquals(original.createdAt, restored.createdAt)
        assertEquals(original.updatedAt, restored.updatedAt)
    }
}
