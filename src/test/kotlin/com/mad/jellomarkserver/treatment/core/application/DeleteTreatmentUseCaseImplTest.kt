package com.mad.jellomarkserver.treatment.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.exception.UnauthorizedTreatmentAccessException
import com.mad.jellomarkserver.treatment.core.domain.model.*
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import com.mad.jellomarkserver.treatment.port.driving.DeleteTreatmentCommand
import com.mad.jellomarkserver.treatment.port.driving.DeleteTreatmentUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class DeleteTreatmentUseCaseImplTest {

    @Mock
    private lateinit var treatmentPort: TreatmentPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: DeleteTreatmentUseCase

    @BeforeEach
    fun setup() {
        useCase = DeleteTreatmentUseCaseImpl(treatmentPort, beautishopPort)
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
            images = ShopImages.empty()
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
    fun `should delete treatment successfully`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()
        val treatment = createTreatment(shop.id)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = DeleteTreatmentCommand(
            treatmentId = treatment.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        useCase.delete(command)

        verify(treatmentPort).delete(treatment.id)
    }

    @Test
    fun `should throw TreatmentNotFoundException when treatment does not exist`() {
        val ownerId = OwnerId.new()
        val treatmentId = TreatmentId.new()

        whenever(treatmentPort.findById(treatmentId)).thenReturn(null)

        val command = DeleteTreatmentCommand(
            treatmentId = treatmentId.value.toString(),
            ownerId = ownerId.value.toString()
        )

        assertFailsWith<TreatmentNotFoundException> {
            useCase.delete(command)
        }
    }

    @Test
    fun `should throw UnauthorizedTreatmentAccessException when owner does not own the shop`() {
        val ownerId = OwnerId.new()
        val otherOwnerShop = createBeautishop()
        val treatment = createTreatment(otherOwnerShop.id)

        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())

        val command = DeleteTreatmentCommand(
            treatmentId = treatment.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        assertFailsWith<UnauthorizedTreatmentAccessException> {
            useCase.delete(command)
        }
    }
}
