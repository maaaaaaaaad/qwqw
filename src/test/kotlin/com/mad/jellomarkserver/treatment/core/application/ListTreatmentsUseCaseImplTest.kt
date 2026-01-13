package com.mad.jellomarkserver.treatment.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.treatment.core.domain.model.*
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import com.mad.jellomarkserver.treatment.port.driving.ListTreatmentsCommand
import com.mad.jellomarkserver.treatment.port.driving.ListTreatmentsUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ListTreatmentsUseCaseImplTest {

    @Mock
    private lateinit var treatmentPort: TreatmentPort

    private lateinit var useCase: ListTreatmentsUseCase

    @BeforeEach
    fun setup() {
        useCase = ListTreatmentsUseCaseImpl(treatmentPort)
    }

    private fun createTreatment(shopId: ShopId, name: String): Treatment {
        return Treatment.reconstruct(
            id = TreatmentId.new(),
            shopId = shopId,
            name = TreatmentName.of(name),
            price = TreatmentPrice.of(50000),
            duration = TreatmentDuration.of(60),
            description = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    @Test
    fun `should return list of treatments for shop`() {
        val shopId = ShopId.new()
        val treatment1 = createTreatment(shopId, "젤네일")
        val treatment2 = createTreatment(shopId, "속눈썹")

        whenever(treatmentPort.findByShopId(shopId)).thenReturn(listOf(treatment1, treatment2))

        val command = ListTreatmentsCommand(shopId = shopId.value.toString())
        val result = useCase.execute(command)

        assertEquals(2, result.size)
        assertEquals("젤네일", result[0].name.value)
        assertEquals("속눈썹", result[1].name.value)
    }

    @Test
    fun `should return empty list when shop has no treatments`() {
        val shopId = ShopId.new()

        whenever(treatmentPort.findByShopId(shopId)).thenReturn(emptyList())

        val command = ListTreatmentsCommand(shopId = shopId.value.toString())
        val result = useCase.execute(command)

        assertTrue(result.isEmpty())
    }
}
