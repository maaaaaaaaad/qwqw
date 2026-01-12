package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.ListBeautishopsCommand
import com.mad.jellomarkserver.beautishop.port.driving.ListBeautishopsUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@ExtendWith(MockitoExtension::class)
class ListBeautishopsUseCaseImplTest {

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: ListBeautishopsUseCase

    @BeforeEach
    fun setup() {
        useCase = ListBeautishopsUseCaseImpl(beautishopPort)
    }

    @Test
    fun `should return paged beautishops`() {
        val beautishops = listOf(
            createBeautishop("Shop A"),
            createBeautishop("Shop B")
        )
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(beautishops, pageable, 2)

        `when`(beautishopPort.findAllPaged(pageable)).thenReturn(page)

        val command = ListBeautishopsCommand(page = 0, size = 20)
        val result = useCase.execute(command)

        assertEquals(2, result.items.size)
        assertEquals("Shop A", result.items[0].name.value)
        assertEquals("Shop B", result.items[1].name.value)
        assertEquals(false, result.hasNext)
        assertEquals(2, result.totalElements)
    }

    @Test
    fun `should return hasNext true when more pages exist`() {
        val beautishops = listOf(
            createBeautishop("Shop A"),
            createBeautishop("Shop B")
        )
        val pageable = PageRequest.of(0, 2)
        val page = PageImpl(beautishops, pageable, 5)

        `when`(beautishopPort.findAllPaged(pageable)).thenReturn(page)

        val command = ListBeautishopsCommand(page = 0, size = 2)
        val result = useCase.execute(command)

        assertEquals(2, result.items.size)
        assertEquals(true, result.hasNext)
        assertEquals(5, result.totalElements)
    }

    @Test
    fun `should return empty list when no beautishops exist`() {
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl<Beautishop>(emptyList(), pageable, 0)

        `when`(beautishopPort.findAllPaged(pageable)).thenReturn(page)

        val command = ListBeautishopsCommand(page = 0, size = 20)
        val result = useCase.execute(command)

        assertEquals(0, result.items.size)
        assertEquals(false, result.hasNext)
        assertEquals(0, result.totalElements)
    }

    private fun createBeautishop(name: String): Beautishop {
        return Beautishop.create(
            name = ShopName.of(name),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구 테헤란로 123"),
            gps = ShopGPS.of(37.5665, 126.9780),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = ShopDescription.of("아름다운 네일샵입니다"),
            image = ShopImage.of("https://example.com/image.jpg")
        )
    }
}
