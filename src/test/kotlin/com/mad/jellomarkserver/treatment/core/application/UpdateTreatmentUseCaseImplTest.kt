package com.mad.jellomarkserver.treatment.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.exception.UnauthorizedTreatmentAccessException
import com.mad.jellomarkserver.treatment.core.domain.model.*
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import com.mad.jellomarkserver.treatment.port.driving.UpdateTreatmentCommand
import com.mad.jellomarkserver.treatment.port.driving.UpdateTreatmentUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.Instant
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class UpdateTreatmentUseCaseImplTest {

    @Mock
    private lateinit var treatmentPort: TreatmentPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: UpdateTreatmentUseCase

    @BeforeEach
    fun setup() {
        useCase = UpdateTreatmentUseCaseImpl(treatmentPort, beautishopPort)
    }

    private fun createBeautishop(): Beautishop {
        return Beautishop.create(
            name = ShopName.of("Test Shop"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구"),
            gps = ShopGPS.of(37.5, 127.0),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = null,
            image = null
        )
    }

    private fun createTreatment(shopId: ShopId): Treatment {
        return Treatment.reconstruct(
            id = TreatmentId.new(),
            shopId = shopId,
            name = TreatmentName.of("젤네일"),
            price = TreatmentPrice.of(50000),
            duration = TreatmentDuration.of(60),
            description = TreatmentDescription.of("기본 젤네일 시술"),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    @Test
    fun `should update treatment successfully`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()
        val treatment = createTreatment(shop.id)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(treatmentPort.save(any())).thenAnswer { invocation ->
            invocation.arguments[0] as Treatment
        }

        val command = UpdateTreatmentCommand(
            treatmentId = treatment.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "속눈썹 연장",
            price = 80000,
            duration = 90,
            description = "속눈썹 연장 시술"
        )

        val result = useCase.update(command)

        assertEquals("속눈썹 연장", result.name.value)
        assertEquals(80000, result.price.value)
        assertEquals(90, result.duration.value)
        assertEquals("속눈썹 연장 시술", result.description?.value)
    }

    @Test
    fun `should throw TreatmentNotFoundException when treatment does not exist`() {
        val ownerId = OwnerId.new()
        val treatmentId = TreatmentId.new()

        whenever(treatmentPort.findById(treatmentId)).thenReturn(null)

        val command = UpdateTreatmentCommand(
            treatmentId = treatmentId.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "속눈썹 연장",
            price = 80000,
            duration = 90,
            description = null
        )

        assertFailsWith<TreatmentNotFoundException> {
            useCase.update(command)
        }
    }

    @Test
    fun `should throw UnauthorizedTreatmentAccessException when owner does not own the shop`() {
        val ownerId = OwnerId.new()
        val otherOwnerShop = createBeautishop()
        val treatment = createTreatment(otherOwnerShop.id)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())

        val command = UpdateTreatmentCommand(
            treatmentId = treatment.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "속눈썹 연장",
            price = 80000,
            duration = 90,
            description = null
        )

        assertFailsWith<UnauthorizedTreatmentAccessException> {
            useCase.update(command)
        }
    }

    @Test
    fun `should update treatment without description`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()
        val treatment = createTreatment(shop.id)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(treatmentPort.save(any())).thenAnswer { invocation ->
            invocation.arguments[0] as Treatment
        }

        val command = UpdateTreatmentCommand(
            treatmentId = treatment.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "속눈썹 연장",
            price = 80000,
            duration = 90,
            description = null
        )

        val result = useCase.update(command)

        assertNull(result.description)
    }
}
