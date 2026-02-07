package com.mad.jellomarkserver.reservation.port.driven

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import java.time.LocalDate
import java.time.LocalTime

interface ReservationPort {
    fun save(reservation: Reservation): Reservation
    fun findById(id: ReservationId): Reservation?
    fun findByMemberId(memberId: MemberId): List<Reservation>
    fun findByShopId(shopId: ShopId): List<Reservation>
    fun findByShopIdAndDate(shopId: ShopId, date: LocalDate): List<Reservation>
    fun existsOverlapping(shopId: ShopId, date: LocalDate, startTime: LocalTime, endTime: LocalTime): Boolean
}
