package com.mad.jellomarkserver.treatment.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.exception.UnauthorizedBeautishopAccessException
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.treatment.core.domain.exception.InvalidTreatmentDurationException
import com.mad.jellomarkserver.treatment.core.domain.exception.InvalidTreatmentNameException
import com.mad.jellomarkserver.treatment.core.domain.exception.InvalidTreatmentPriceException
import com.mad.jellomarkserver.treatment.core.domain.model.Treatment
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import com.mad.jellomarkserver.treatment.port.driving.CreateTreatmentCommand
import com.mad.jellomarkserver.treatment.port.driving.CreateTreatmentUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class CreateTreatmentUseCaseImplTest {

    @Mock
    private lateinit var treatmentPort: TreatmentPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: CreateTreatmentUseCase

    @BeforeEach
    fun setup() {
        useCase = CreateTreatmentUseCaseImpl(treatmentPort, beautishopPort)
    }

    private fun createBeautishop(ownerId: OwnerId): Beautishop {
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

    @Test
    fun `should create treatment successfully with all fields`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop(ownerId)

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(treatmentPort.save(any())).thenAnswer { invocation ->
            invocation.arguments[0] as Treatment
        }

        val command = CreateTreatmentCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "젤네일",
            price = 50000,
            duration = 60,
            description = "기본 젤네일 시술입니다"
        )

        val result = useCase.create(command)

        assertNotNull(result)
        assertNotNull(result.id)
        assertEquals(shop.id, result.shopId)
        assertEquals("젤네일", result.name.value)
        assertEquals(50000, result.price.value)
        assertEquals(60, result.duration.value)
        assertEquals("기본 젤네일 시술입니다", result.description?.value)
        assertNotNull(result.createdAt)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `should create treatment successfully without description`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop(ownerId)

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(treatmentPort.save(any())).thenAnswer { invocation ->
            invocation.arguments[0] as Treatment
        }

        val command = CreateTreatmentCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "젤네일",
            price = 50000,
            duration = 60,
            description = null
        )

        val result = useCase.create(command)

        assertNotNull(result)
        assertNull(result.description)
    }

    @Test
    fun `should throw BeautishopNotFoundException when shop does not exist`() {
        val ownerId = OwnerId.new()
        val nonExistentShopId = ShopId.new()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(nonExistentShopId)).thenReturn(null)

        val command = CreateTreatmentCommand(
            shopId = nonExistentShopId.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "젤네일",
            price = 50000,
            duration = 60,
            description = null
        )

        assertFailsWith<BeautishopNotFoundException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw UnauthorizedBeautishopAccessException when owner does not own the shop`() {
        val ownerId = OwnerId.new()
        val otherOwnersShop = createBeautishop(OwnerId.new())

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(otherOwnersShop.id)).thenReturn(otherOwnersShop)

        val command = CreateTreatmentCommand(
            shopId = otherOwnersShop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "젤네일",
            price = 50000,
            duration = 60,
            description = null
        )

        assertFailsWith<UnauthorizedBeautishopAccessException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidTreatmentNameException when name is blank`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop(ownerId)

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = CreateTreatmentCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "   ",
            price = 50000,
            duration = 60,
            description = null
        )

        assertFailsWith<InvalidTreatmentNameException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidTreatmentNameException when name is too short`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop(ownerId)

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = CreateTreatmentCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "A",
            price = 50000,
            duration = 60,
            description = null
        )

        assertFailsWith<InvalidTreatmentNameException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidTreatmentPriceException when price is negative`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop(ownerId)

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = CreateTreatmentCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "젤네일",
            price = -1,
            duration = 60,
            description = null
        )

        assertFailsWith<InvalidTreatmentPriceException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidTreatmentDurationException when duration is too short`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop(ownerId)

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = CreateTreatmentCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "젤네일",
            price = 50000,
            duration = 5,
            description = null
        )

        assertFailsWith<InvalidTreatmentDurationException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidTreatmentDurationException when duration is too long`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop(ownerId)

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = CreateTreatmentCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "젤네일",
            price = 50000,
            duration = 400,
            description = null
        )

        assertFailsWith<InvalidTreatmentDurationException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should create treatment with zero price`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop(ownerId)

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(treatmentPort.save(any())).thenAnswer { invocation ->
            invocation.arguments[0] as Treatment
        }

        val command = CreateTreatmentCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "무료 상담",
            price = 0,
            duration = 30,
            description = null
        )

        val result = useCase.create(command)

        assertEquals(0, result.price.value)
    }

    @Test
    fun `should create treatment with minimum duration`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop(ownerId)

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(treatmentPort.save(any())).thenAnswer { invocation ->
            invocation.arguments[0] as Treatment
        }

        val command = CreateTreatmentCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "빠른 시술",
            price = 10000,
            duration = 10,
            description = null
        )

        val result = useCase.create(command)

        assertEquals(10, result.duration.value)
    }

    @Test
    fun `should create treatment with maximum duration`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop(ownerId)

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(treatmentPort.save(any())).thenAnswer { invocation ->
            invocation.arguments[0] as Treatment
        }

        val command = CreateTreatmentCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            treatmentName = "풀 코스",
            price = 200000,
            duration = 300,
            description = null
        )

        val result = useCase.create(command)

        assertEquals(300, result.duration.value)
    }
}
