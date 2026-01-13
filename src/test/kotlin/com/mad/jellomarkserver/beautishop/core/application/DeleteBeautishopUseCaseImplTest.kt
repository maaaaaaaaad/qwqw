package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.exception.UnauthorizedBeautishopAccessException
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.DeleteBeautishopCommand
import com.mad.jellomarkserver.beautishop.port.driving.DeleteBeautishopUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class DeleteBeautishopUseCaseImplTest {

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: DeleteBeautishopUseCase

    @BeforeEach
    fun setup() {
        useCase = DeleteBeautishopUseCaseImpl(beautishopPort)
    }

    @Test
    fun `should delete beautishop successfully when owner is authorized`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = DeleteBeautishopCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        useCase.delete(command)

        verify(beautishopPort).delete(shop.id)
    }

    @Test
    fun `should throw BeautishopNotFoundException when shop does not exist`() {
        val ownerId = OwnerId.new()
        val shopId = ShopId.new()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(shopId)).thenReturn(null)

        val command = DeleteBeautishopCommand(
            shopId = shopId.value.toString(),
            ownerId = ownerId.value.toString()
        )

        assertFailsWith<BeautishopNotFoundException> {
            useCase.delete(command)
        }
    }

    @Test
    fun `should throw UnauthorizedBeautishopAccessException when owner does not own the shop`() {
        val ownerId = OwnerId.new()
        val otherOwnersShop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(otherOwnersShop.id)).thenReturn(otherOwnersShop)

        val command = DeleteBeautishopCommand(
            shopId = otherOwnersShop.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        val exception = assertFailsWith<UnauthorizedBeautishopAccessException> {
            useCase.delete(command)
        }

        assertEquals(otherOwnersShop.id.value.toString(), exception.shopId)
    }

    @Test
    fun `should not call delete when unauthorized`() {
        val ownerId = OwnerId.new()
        val otherOwnersShop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(otherOwnersShop.id)).thenReturn(otherOwnersShop)

        val command = DeleteBeautishopCommand(
            shopId = otherOwnersShop.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        try {
            useCase.delete(command)
        } catch (e: UnauthorizedBeautishopAccessException) {
        }

        verify(beautishopPort, org.mockito.kotlin.never()).delete(otherOwnersShop.id)
    }

    private fun createBeautishop(): Beautishop {
        return Beautishop.create(
            name = ShopName.of("Test Shop"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구"),
            gps = ShopGPS.of(37.5, 127.0),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = ShopDescription.of("Test description"),
            image = ShopImage.of("https://example.com/image.jpg")
        )
    }
}
