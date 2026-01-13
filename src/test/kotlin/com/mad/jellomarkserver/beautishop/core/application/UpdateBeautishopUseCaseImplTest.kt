package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.*
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopCommand
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
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
class UpdateBeautishopUseCaseImplTest {

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: UpdateBeautishopUseCase

    @BeforeEach
    fun setup() {
        useCase = UpdateBeautishopUseCaseImpl(beautishopPort)
    }

    @Test
    fun `should update beautishop successfully when owner is authorized`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(beautishopPort.save(any(), any())).thenAnswer { it.arguments[0] as Beautishop }

        val command = UpdateBeautishopCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            operatingTime = mapOf("tuesday" to "10:00-20:00"),
            shopDescription = "Updated description",
            shopImage = "https://example.com/new-image.jpg"
        )

        val result = useCase.update(command)

        assertEquals(shop.id, result.id)
        assertEquals(mapOf("tuesday" to "10:00-20:00"), result.operatingTime.schedule)
        assertEquals("Updated description", result.description?.value)
        assertEquals("https://example.com/new-image.jpg", result.image?.value)
    }

    @Test
    fun `should keep existing operatingTime when operatingTime is null`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(beautishopPort.save(any(), any())).thenAnswer { it.arguments[0] as Beautishop }

        val command = UpdateBeautishopCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            operatingTime = null,
            shopDescription = "Updated description",
            shopImage = null
        )

        val result = useCase.update(command)

        assertEquals(shop.operatingTime.schedule, result.operatingTime.schedule)
        assertEquals("Updated description", result.description?.value)
        assertNull(result.image)
    }

    @Test
    fun `should update description to null`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(beautishopPort.save(any(), any())).thenAnswer { it.arguments[0] as Beautishop }

        val command = UpdateBeautishopCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = null
        )

        val result = useCase.update(command)

        assertNull(result.description)
        assertNull(result.image)
    }

    @Test
    fun `should throw BeautishopNotFoundException when shop does not exist`() {
        val ownerId = OwnerId.new()
        val shopId = ShopId.new()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(shopId)).thenReturn(null)

        val command = UpdateBeautishopCommand(
            shopId = shopId.value.toString(),
            ownerId = ownerId.value.toString(),
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = null
        )

        assertFailsWith<BeautishopNotFoundException> {
            useCase.update(command)
        }
    }

    @Test
    fun `should throw UnauthorizedBeautishopAccessException when owner does not own the shop`() {
        val ownerId = OwnerId.new()
        val otherOwnersShop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(otherOwnersShop.id)).thenReturn(otherOwnersShop)

        val command = UpdateBeautishopCommand(
            shopId = otherOwnersShop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = null
        )

        val exception = assertFailsWith<UnauthorizedBeautishopAccessException> {
            useCase.update(command)
        }

        assertEquals(otherOwnersShop.id.value.toString(), exception.shopId)
    }

    @Test
    fun `should throw InvalidOperatingTimeException when operatingTime is empty`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = UpdateBeautishopCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            operatingTime = emptyMap(),
            shopDescription = null,
            shopImage = null
        )

        assertFailsWith<InvalidOperatingTimeException> {
            useCase.update(command)
        }
    }

    @Test
    fun `should throw InvalidShopDescriptionException when description is too long`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = UpdateBeautishopCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = "a".repeat(501),
            shopImage = null
        )

        assertFailsWith<InvalidShopDescriptionException> {
            useCase.update(command)
        }
    }

    @Test
    fun `should throw InvalidShopImageException when image URL is invalid`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = UpdateBeautishopCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = "not-a-valid-url"
        )

        assertFailsWith<InvalidShopImageException> {
            useCase.update(command)
        }
    }

    @Test
    fun `should update updatedAt timestamp`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()
        val originalUpdatedAt = shop.updatedAt

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(beautishopPort.save(any(), any())).thenAnswer { it.arguments[0] as Beautishop }

        val command = UpdateBeautishopCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            operatingTime = mapOf("tuesday" to "10:00-20:00"),
            shopDescription = "Updated",
            shopImage = null
        )

        val result = useCase.update(command)

        assertTrue(result.updatedAt >= originalUpdatedAt)
    }

    @Test
    fun `should preserve immutable fields after update`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(beautishopPort.save(any(), any())).thenAnswer { it.arguments[0] as Beautishop }

        val command = UpdateBeautishopCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            operatingTime = mapOf("tuesday" to "10:00-20:00"),
            shopDescription = "Updated",
            shopImage = "https://example.com/new.jpg"
        )

        val result = useCase.update(command)

        assertEquals(shop.id, result.id)
        assertEquals(shop.name, result.name)
        assertEquals(shop.regNum, result.regNum)
        assertEquals(shop.phoneNumber, result.phoneNumber)
        assertEquals(shop.address, result.address)
        assertEquals(shop.gps, result.gps)
        assertEquals(shop.averageRating, result.averageRating)
        assertEquals(shop.reviewCount, result.reviewCount)
        assertEquals(shop.createdAt, result.createdAt)
    }

    private fun createBeautishop(): Beautishop {
        return Beautishop.create(
            name = ShopName.of("Test Shop"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구"),
            gps = ShopGPS.of(37.5, 127.0),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = ShopDescription.of("Original description"),
            image = ShopImage.of("https://example.com/image.jpg")
        )
    }
}
