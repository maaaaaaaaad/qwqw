package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.notification.port.driving.SendNotificationCommand
import com.mad.jellomarkserver.notification.port.driving.SendNotificationUseCase
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.NotifyUnprocessedReservationsUseCase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap

@Service
class NotifyUnprocessedReservationsUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val beautishopPort: BeautishopPort,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val clock: Clock = Clock.system(KST_ZONE)
) : NotifyUnprocessedReservationsUseCase {

    private val log = LoggerFactory.getLogger(NotifyUnprocessedReservationsUseCaseImpl::class.java)
    private val notifiedShops = ConcurrentHashMap<String, Boolean>()

    override fun execute() {
        val today = LocalDate.now(clock)
        val now = LocalTime.now(clock)
        val dayOfWeek = today.dayOfWeek.name.lowercase()

        val confirmedReservations = reservationPort.findByStatusAndDate(ReservationStatus.CONFIRMED, today)
        if (confirmedReservations.isEmpty()) return

        val reservationsByShop = confirmedReservations.groupBy { it.shopId }
        val shopIds = reservationsByShop.keys.toList()
        val shopsById = beautishopPort.findByIds(shopIds).associateBy { it.id }

        reservationsByShop.forEach { (shopId, reservations) ->
            processShop(shopId, reservations, shopsById, dayOfWeek, now, today)
        }
    }

    private fun processShop(
        shopId: ShopId,
        reservations: List<Reservation>,
        shopsById: Map<ShopId, Beautishop>,
        dayOfWeek: String,
        now: LocalTime,
        today: LocalDate
    ) {
        try {
            val deduplicationKey = "${shopId.value}:$today"
            if (notifiedShops.containsKey(deduplicationKey)) return

            val shop = shopsById[shopId] ?: return
            val closingTime = parseClosingTime(shop, dayOfWeek) ?: return

            if (now.isBefore(closingTime)) return

            val ownerId = beautishopPort.findOwnerIdByShopId(shopId) ?: return
            val shopName = shop.name.value

            sendNotificationUseCase.execute(
                SendNotificationCommand(
                    userId = ownerId.value.toString(),
                    userRole = "OWNER",
                    title = "미처리 예약이 있습니다",
                    body = "$shopName - 미처리 예약 ${reservations.size}건을 확인해주세요",
                    type = "UNPROCESSED_RESERVATION_REMINDER",
                    data = mapOf("shopId" to shopId.value.toString())
                )
            )

            notifiedShops[deduplicationKey] = true
        } catch (e: Exception) {
            log.warn("Failed to notify unprocessed reservations for shop {}: {}", shopId.value, e.message)
        }
    }

    private fun parseClosingTime(shop: Beautishop, dayOfWeek: String): LocalTime? {
        val timeRange = shop.operatingTime.schedule[dayOfWeek] ?: return null
        if (timeRange == CLOSED) return null

        val closingTimeStr = timeRange.split("-").getOrNull(1) ?: return null
        val parts = closingTimeStr.split(":")
        return LocalTime.of(parts[0].toInt(), parts[1].toInt())
    }

    companion object {
        private val KST_ZONE = ZoneId.of("Asia/Seoul")
        private const val CLOSED = "closed"
    }
}
