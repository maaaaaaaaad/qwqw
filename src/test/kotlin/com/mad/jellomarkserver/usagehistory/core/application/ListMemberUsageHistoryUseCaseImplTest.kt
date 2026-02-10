package com.mad.jellomarkserver.usagehistory.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationId
import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory
import com.mad.jellomarkserver.usagehistory.port.driven.UsageHistoryPort
import com.mad.jellomarkserver.usagehistory.port.driving.ListMemberUsageHistoryCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ListMemberUsageHistoryUseCaseImplTest {

    @Mock
    private lateinit var usageHistoryPort: UsageHistoryPort

    private lateinit var useCase: ListMemberUsageHistoryUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = ListMemberUsageHistoryUseCaseImpl(usageHistoryPort)
    }

    @Test
    fun `should return usage histories sorted by completedAt descending`() {
        val memberId = MemberId.new()
        val older = UsageHistory.reconstruct(
            id = com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistoryId.new(),
            memberId = memberId,
            shopId = ShopId.new(),
            reservationId = ReservationId.new(),
            shopName = "샵A",
            treatmentName = "네일A",
            treatmentPrice = 30000,
            treatmentDuration = 60,
            completedAt = Instant.parse("2025-06-10T10:00:00Z"),
            createdAt = Instant.parse("2025-06-10T10:00:00Z")
        )
        val newer = UsageHistory.reconstruct(
            id = com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistoryId.new(),
            memberId = memberId,
            shopId = ShopId.new(),
            reservationId = ReservationId.new(),
            shopName = "샵B",
            treatmentName = "네일B",
            treatmentPrice = 50000,
            treatmentDuration = 90,
            completedAt = Instant.parse("2025-06-15T14:00:00Z"),
            createdAt = Instant.parse("2025-06-15T14:00:00Z")
        )

        whenever(usageHistoryPort.findByMemberId(any()))
            .thenReturn(listOf(older, newer))

        val command = ListMemberUsageHistoryCommand(memberId.value.toString())
        val result = useCase.execute(command)

        assertEquals(2, result.size)
        assertEquals("샵B", result[0].shopName)
        assertEquals("샵A", result[1].shopName)
    }

    @Test
    fun `should return empty list when no usage history exists`() {
        val memberId = MemberId.new()
        whenever(usageHistoryPort.findByMemberId(any()))
            .thenReturn(emptyList())

        val command = ListMemberUsageHistoryCommand(memberId.value.toString())
        val result = useCase.execute(command)

        assertTrue(result.isEmpty())
    }
}
