package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.GetBeautishopCommand
import com.mad.jellomarkserver.beautishop.port.driving.GetBeautishopUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class GetBeautishopUseCaseImplTest {

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: GetBeautishopUseCase

    @BeforeEach
    fun setup() {
        useCase = GetBeautishopUseCaseImpl(beautishopPort)
    }

    @Test
    fun `should return beautishop when found by id`() {
        val shopId = ShopId.new()
        val beautishop = Beautishop.create(
            name = ShopName.of("Beautiful Salon"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구 테헤란로 123"),
            gps = ShopGPS.of(37.5665, 126.9780),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = ShopDescription.of("아름다운 네일샵입니다"),
            images = ShopImages.of(listOf("https://example.com/image.jpg"))
        )

        `when`(beautishopPort.findById(shopId)).thenReturn(beautishop)

        val command = GetBeautishopCommand(shopId = shopId.value.toString())
        val result = useCase.execute(command)

        assertNotNull(result)
        assertEquals("Beautiful Salon", result.name.value)
        assertEquals("123-45-67890", result.regNum.value)
    }

    @Test
    fun `should throw BeautishopNotFoundException when shop not found`() {
        val shopId = ShopId.new()

        `when`(beautishopPort.findById(shopId)).thenReturn(null)

        val command = GetBeautishopCommand(shopId = shopId.value.toString())

        val exception = assertFailsWith<BeautishopNotFoundException> {
            useCase.execute(command)
        }

        assertTrue(exception.message!!.contains(shopId.value.toString()))
    }

    @Test
    fun `should throw IllegalArgumentException when shopId is invalid UUID`() {
        val command = GetBeautishopCommand(shopId = "invalid-uuid")

        assertFailsWith<IllegalArgumentException> {
            useCase.execute(command)
        }
    }
}
