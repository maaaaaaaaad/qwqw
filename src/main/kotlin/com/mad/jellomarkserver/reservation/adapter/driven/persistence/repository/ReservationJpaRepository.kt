package com.mad.jellomarkserver.reservation.adapter.driven.persistence.repository

import com.mad.jellomarkserver.reservation.adapter.driven.persistence.entity.ReservationJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

interface ReservationJpaRepository : JpaRepository<ReservationJpaEntity, UUID> {
    fun findByMemberId(memberId: UUID): List<ReservationJpaEntity>
    fun findByShopId(shopId: UUID): List<ReservationJpaEntity>
    fun findByShopIdAndReservationDate(shopId: UUID, reservationDate: LocalDate): List<ReservationJpaEntity>

    @Query(
        """
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM ReservationJpaEntity r
        WHERE r.shopId = :shopId
          AND r.reservationDate = :date
          AND r.status IN ('PENDING', 'CONFIRMED')
          AND r.startTime < :endTime
          AND r.endTime > :startTime
        """
    )
    fun existsOverlapping(
        shopId: UUID,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime
    ): Boolean
}
