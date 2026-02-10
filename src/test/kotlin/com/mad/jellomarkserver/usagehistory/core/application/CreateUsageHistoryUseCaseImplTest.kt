package com.mad.jellomarkserver.usagehistory.core.application

import com.mad.jellomarkserver.usagehistory.core.domain.model.UsageHistory
import com.mad.jellomarkserver.usagehistory.port.driven.UsageHistoryPort
import com.mad.jellomarkserver.usagehistory.port.driving.CreateUsageHistoryCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

@ExtendWith(MockitoExtension::class)
class CreateUsageHistoryUseCaseImplTest {

    @Mock
    private lateinit var usageHistoryPort: UsageHistoryPort

    private lateinit var useCase: CreateUsageHistoryUseCaseImpl

    private val fixedClock = Clock.fixed(Instant.parse("2025-06-15T14:00:00Z"), ZoneId.of("UTC"))

    @BeforeEach
    fun setup() {
        useCase = CreateUsageHistoryUseCaseImpl(usageHistoryPort, fixedClock)
    }

    @Test
    fun `should create usage history successfully`() {
        whenever(usageHistoryPort.save(any())).thenAnswer { it.arguments[0] as UsageHistory }

        val command = CreateUsageHistoryCommand(
            memberId = UUID.randomUUID().toString(),
            shopId = UUID.randomUUID().toString(),
            reservationId = UUID.randomUUID().toString(),
            shopName = "젤로네일",
            treatmentName = "젤네일",
            treatmentPrice = 50000,
            treatmentDuration = 60,
            completedAt = "2025-06-15T13:00:00Z"
        )

        val result = useCase.execute(command)

        assertNotNull(result.id)
        assertEquals(command.shopName, result.shopName)
        assertEquals(command.treatmentName, result.treatmentName)
        assertEquals(command.treatmentPrice, result.treatmentPrice)
        assertEquals(command.treatmentDuration, result.treatmentDuration)
        assertEquals(Instant.parse(command.completedAt), result.completedAt)
    }
}
