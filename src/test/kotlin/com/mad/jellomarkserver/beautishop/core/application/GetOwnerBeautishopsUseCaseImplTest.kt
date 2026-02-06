package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.GetOwnerBeautishopsCommand
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.*
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class GetOwnerBeautishopsUseCaseImplTest {

    @Mock
    private lateinit var ownerPort: OwnerPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: GetOwnerBeautishopsUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = GetOwnerBeautishopsUseCaseImpl(ownerPort, beautishopPort)
    }

    @Test
    fun `should return beautishops owned by the owner`() {
        val owner = Owner.create(
            businessNumber = BusinessNumber.of("123456789"),
            ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678"),
            ownerNickname = OwnerNickname.of("test"),
            ownerEmail = OwnerEmail.of("owner@example.com")
        )

        val shop1 = Beautishop.create(
            name = ShopName.of("Shop One"),
            regNum = ShopRegNum.of("111-11-11111"),
            phoneNumber = ShopPhoneNumber.of("010-1111-1111"),
            address = ShopAddress.of("Seoul"),
            gps = ShopGPS.of(37.5665, 126.9780),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = null,
            images = ShopImages.empty(),
        )

        val shop2 = Beautishop.create(
            name = ShopName.of("Shop Two"),
            regNum = ShopRegNum.of("222-22-22222"),
            phoneNumber = ShopPhoneNumber.of("010-2222-2222"),
            address = ShopAddress.of("Busan"),
            gps = ShopGPS.of(35.1796, 129.0756),
            operatingTime = OperatingTime.of(mapOf("monday" to "10:00-19:00")),
            description = null,
            images = ShopImages.empty(),
        )

        whenever(ownerPort.findByEmail(any())).thenReturn(owner)
        whenever(beautishopPort.findByOwnerId(owner.id)).thenReturn(listOf(shop1, shop2))

        val command = GetOwnerBeautishopsCommand(email = "owner@example.com")
        val result = useCase.execute(command)

        assertEquals(2, result.size)
        assertEquals("Shop One", result[0].name.value)
        assertEquals("Shop Two", result[1].name.value)
    }

    @Test
    fun `should return empty list when owner has no shops`() {
        val owner = Owner.create(
            businessNumber = BusinessNumber.of("123456789"),
            ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678"),
            ownerNickname = OwnerNickname.of("test"),
            ownerEmail = OwnerEmail.of("owner@example.com")
        )

        whenever(ownerPort.findByEmail(any())).thenReturn(owner)
        whenever(beautishopPort.findByOwnerId(owner.id)).thenReturn(emptyList())

        val command = GetOwnerBeautishopsCommand(email = "owner@example.com")
        val result = useCase.execute(command)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should throw OwnerNotFoundException when owner not found`() {
        whenever(ownerPort.findByEmail(any())).thenReturn(null)

        val command = GetOwnerBeautishopsCommand(email = "nonexistent@example.com")

        val exception = assertThrows(OwnerNotFoundException::class.java) {
            useCase.execute(command)
        }
        assertTrue(exception.message!!.contains("nonexistent@example.com"))
    }
}
