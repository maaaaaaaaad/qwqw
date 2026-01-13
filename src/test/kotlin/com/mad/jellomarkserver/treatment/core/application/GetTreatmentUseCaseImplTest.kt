package com.mad.jellomarkserver.treatment.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.model.*
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import com.mad.jellomarkserver.treatment.port.driving.GetTreatmentCommand
import com.mad.jellomarkserver.treatment.port.driving.GetTreatmentUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.Instant
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class GetTreatmentUseCaseImplTest {

    @Mock
    private lateinit var treatmentPort: TreatmentPort

    private lateinit var useCase: GetTreatmentUseCase

    @BeforeEach
    fun setup() {
        useCase = GetTreatmentUseCaseImpl(treatmentPort)
    }

    private fun createTreatment(): Treatment {
        return Treatment.reconstruct(
            id = TreatmentId.new(),
            shopId = ShopId.new(),
            name = TreatmentName.of("젤네일"),
            price = TreatmentPrice.of(50000),
            duration = TreatmentDuration.of(60),
            description = TreatmentDescription.of("기본 젤네일 시술"),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    @Test
    fun `should return treatment when it exists`() {
        val treatment = createTreatment()

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)

        val command = GetTreatmentCommand(treatmentId = treatment.id.value.toString())
        val result = useCase.execute(command)

        assertEquals(treatment.id, result.id)
        assertEquals(treatment.name, result.name)
        assertEquals(treatment.price, result.price)
    }

    @Test
    fun `should throw TreatmentNotFoundException when treatment does not exist`() {
        val treatmentId = TreatmentId.new()

        whenever(treatmentPort.findById(treatmentId)).thenReturn(null)

        val command = GetTreatmentCommand(treatmentId = treatmentId.value.toString())

        assertFailsWith<TreatmentNotFoundException> {
            useCase.execute(command)
        }
    }
}
