package com.mad.jellomarkserver.reservation.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.adapter.driven.persistence.mapper.ReservationMapper
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalTime

@Component
class ReservationPersistenceAdapter(
    private val repository: ReservationJpaRepository,
    private val mapper: ReservationMapper
) : ReservationPort {

    override fun save(reservation: Reservation): Reservation {
        val entity = mapper.toEntity(reservation)
        val saved = repository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun findById(id: ReservationId): Reservation? {
        return repository.findById(id.value)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByMemberId(memberId: MemberId): List<Reservation> {
        return repository.findByMemberId(memberId.value)
            .map { mapper.toDomain(it) }
    }

    override fun findByShopId(shopId: ShopId): List<Reservation> {
        return repository.findByShopId(shopId.value)
            .map { mapper.toDomain(it) }
    }

    override fun findByShopIdAndDate(shopId: ShopId, date: LocalDate): List<Reservation> {
        return repository.findByShopIdAndReservationDate(shopId.value, date)
            .map { mapper.toDomain(it) }
    }

    override fun existsOverlapping(
        shopId: ShopId,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime
    ): Boolean {
        return repository.existsOverlapping(shopId.value, date, startTime, endTime)
    }
}
