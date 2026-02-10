package com.mad.jellomarkserver.usagehistory.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory
import com.mad.jellomarkserver.usagehistory.port.driven.UsageHistoryPort
import com.mad.jellomarkserver.usagehistory.port.driving.CreateUsageHistoryCommand
import com.mad.jellomarkserver.usagehistory.port.driving.CreateUsageHistoryUseCase
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.*

@Service
class CreateUsageHistoryUseCaseImpl(
    private val usageHistoryPort: UsageHistoryPort,
    private val clock: Clock = Clock.systemUTC()
) : CreateUsageHistoryUseCase {

    override fun execute(command: CreateUsageHistoryCommand): UsageHistory {
        val usageHistory = UsageHistory.create(
            memberId = MemberId.from(UUID.fromString(command.memberId)),
            shopId = ShopId.from(UUID.fromString(command.shopId)),
            reservationId = ReservationId.from(UUID.fromString(command.reservationId)),
            shopName = command.shopName,
            treatmentName = command.treatmentName,
            treatmentPrice = command.treatmentPrice,
            treatmentDuration = command.treatmentDuration,
            completedAt = Instant.parse(command.completedAt),
            clock = clock
        )

        return usageHistoryPort.save(usageHistory)
    }
}
