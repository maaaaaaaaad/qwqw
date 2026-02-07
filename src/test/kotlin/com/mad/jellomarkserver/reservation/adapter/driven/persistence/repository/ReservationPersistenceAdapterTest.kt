package com.mad.jellomarkserver.reservation.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.adapter.driven.persistence.entity.ReservationJpaEntity
import com.mad.jellomarkserver.reservation.adapter.driven.persistence.mapper.ReservationMapper
import com.mad.jellomarkserver.reservation.core.domain.model.*
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class ReservationPersistenceAdapterTest {

    @Mock
    private lateinit var jpaRepository: ReservationJpaRepository

    @Mock
    private lateinit var mapper: ReservationMapper

    private lateinit var adapter: ReservationPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = ReservationPersistenceAdapter(jpaRepository, mapper)
    }

    @Test
    fun `should save reservation successfully`() {
        val reservation = createReservation()
        val entity = createEntity()

        `when`(mapper.toEntity(reservation)).thenReturn(entity)
        `when`(jpaRepository.save(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(reservation)

        val result = adapter.save(reservation)

        assertEquals(reservation, result)
        verify(mapper).toEntity(reservation)
        verify(jpaRepository).save(entity)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should find reservation by id`() {
        val reservationId = ReservationId.new()
        val reservation = createReservation()
        val entity = createEntity()

        `when`(jpaRepository.findById(reservationId.value)).thenReturn(Optional.of(entity))
        `when`(mapper.toDomain(entity)).thenReturn(reservation)

        val result = adapter.findById(reservationId)

        assertEquals(reservation, result)
    }

    @Test
    fun `should return null when reservation not found by id`() {
        val reservationId = ReservationId.new()

        `when`(jpaRepository.findById(reservationId.value)).thenReturn(Optional.empty())

        val result = adapter.findById(reservationId)

        assertNull(result)
    }

    @Test
    fun `should find reservations by memberId`() {
        val memberId = MemberId.new()
        val entity1 = createEntity()
        val entity2 = createEntity()
        val reservation1 = createReservation()
        val reservation2 = createReservation()

        `when`(jpaRepository.findByMemberId(memberId.value)).thenReturn(listOf(entity1, entity2))
        `when`(mapper.toDomain(entity1)).thenReturn(reservation1)
        `when`(mapper.toDomain(entity2)).thenReturn(reservation2)

        val result = adapter.findByMemberId(memberId)

        assertEquals(2, result.size)
    }

    @Test
    fun `should find reservations by shopId`() {
        val shopId = ShopId.new()
        val entity = createEntity()
        val reservation = createReservation()

        `when`(jpaRepository.findByShopId(shopId.value)).thenReturn(listOf(entity))
        `when`(mapper.toDomain(entity)).thenReturn(reservation)

        val result = adapter.findByShopId(shopId)

        assertEquals(1, result.size)
    }

    @Test
    fun `should find reservations by shopId and date`() {
        val shopId = ShopId.new()
        val date = LocalDate.of(2025, 3, 15)
        val entity = createEntity()
        val reservation = createReservation()

        `when`(jpaRepository.findByShopIdAndReservationDate(shopId.value, date)).thenReturn(listOf(entity))
        `when`(mapper.toDomain(entity)).thenReturn(reservation)

        val result = adapter.findByShopIdAndDate(shopId, date)

        assertEquals(1, result.size)
    }

    @Test
    fun `should check overlapping reservations`() {
        val shopId = ShopId.new()
        val date = LocalDate.of(2025, 3, 15)
        val startTime = LocalTime.of(14, 0)
        val endTime = LocalTime.of(15, 0)

        `when`(jpaRepository.existsOverlapping(shopId.value, date, startTime, endTime)).thenReturn(true)

        val result = adapter.existsOverlapping(shopId, date, startTime, endTime)

        assertTrue(result)
    }

    @Test
    fun `should return false when no overlapping reservations`() {
        val shopId = ShopId.new()
        val date = LocalDate.of(2025, 3, 15)
        val startTime = LocalTime.of(14, 0)
        val endTime = LocalTime.of(15, 0)

        `when`(jpaRepository.existsOverlapping(shopId.value, date, startTime, endTime)).thenReturn(false)

        val result = adapter.existsOverlapping(shopId, date, startTime, endTime)

        assertFalse(result)
    }

    private fun createReservation(): Reservation {
        return Reservation.create(
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = null
        )
    }

    private fun createEntity(): ReservationJpaEntity {
        return ReservationJpaEntity(
            id = UUID.randomUUID(),
            shopId = UUID.randomUUID(),
            memberId = UUID.randomUUID(),
            treatmentId = UUID.randomUUID(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            status = "PENDING",
            memo = null,
            rejectionReason = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
