package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.ListBeautishopsCommand
import com.mad.jellomarkserver.beautishop.port.driving.ListBeautishopsUseCase
import com.mad.jellomarkserver.beautishop.port.driving.SortBy
import com.mad.jellomarkserver.beautishop.port.driving.SortOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
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

        whenever(beautishopPort.findAllFiltered(any(), any())).thenReturn(page)

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

        whenever(beautishopPort.findAllFiltered(any(), any())).thenReturn(page)

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

        whenever(beautishopPort.findAllFiltered(any(), any())).thenReturn(page)

        val command = ListBeautishopsCommand(page = 0, size = 20)
        val result = useCase.execute(command)

        assertEquals(0, result.items.size)
        assertEquals(false, result.hasNext)
        assertEquals(0, result.totalElements)
    }

    @Test
    fun `should calculate distance when latitude and longitude provided`() {
        val beautishops = listOf(
            createBeautishopWithGps("Shop A", 37.5665, 126.9780),
            createBeautishopWithGps("Shop B", 37.4979, 127.0276)
        )
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(beautishops, pageable, 2)

        whenever(beautishopPort.findAllFiltered(any(), any())).thenReturn(page)

        val command = ListBeautishopsCommand(
            page = 0,
            size = 20,
            latitude = 37.5000,
            longitude = 127.0000
        )
        val result = useCase.execute(command)

        assertEquals(2, result.items.size)
        assertNotNull(result.distances[0])
        assertNotNull(result.distances[1])
    }

    @Test
    fun `should sort by distance when sortBy is DISTANCE`() {
        val beautishops = listOf(
            createBeautishopWithGps("Shop A", 37.5665, 126.9780),
            createBeautishopWithGps("Shop B", 37.4979, 127.0276)
        )
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(beautishops, pageable, 2)

        whenever(beautishopPort.findAllFiltered(any(), any())).thenReturn(page)

        val command = ListBeautishopsCommand(
            page = 0,
            size = 20,
            sortBy = SortBy.DISTANCE,
            sortOrder = SortOrder.ASC,
            latitude = 37.4979,
            longitude = 127.0276
        )
        val result = useCase.execute(command)

        assertEquals("Shop B", result.items[0].name.value)
    }

    @Test
    fun `should filter by radiusKm when provided`() {
        val nearShop = createBeautishopWithGps("Near Shop", 37.5010, 127.0010)
        val farShop = createBeautishopWithGps("Far Shop", 37.6000, 127.1000)
        val beautishops = listOf(nearShop, farShop)

        whenever(beautishopPort.findAllFilteredWithoutPaging(any())).thenReturn(beautishops)

        val command = ListBeautishopsCommand(
            page = 0,
            size = 20,
            latitude = 37.5000,
            longitude = 127.0000,
            radiusKm = 5.0
        )
        val result = useCase.execute(command)

        assertEquals(1, result.items.size)
        assertEquals("Near Shop", result.items[0].name.value)
    }

    @Test
    fun `should return empty when no shops within radius`() {
        val farShop = createBeautishopWithGps("Far Shop", 38.0000, 128.0000)
        val beautishops = listOf(farShop)

        whenever(beautishopPort.findAllFilteredWithoutPaging(any())).thenReturn(beautishops)

        val command = ListBeautishopsCommand(
            page = 0,
            size = 20,
            latitude = 37.5000,
            longitude = 127.0000,
            radiusKm = 5.0
        )
        val result = useCase.execute(command)

        assertEquals(0, result.items.size)
    }

    @Test
    fun `should paginate radius filtered results`() {
        val shops = (1..10).map { i ->
            createBeautishopWithGps("Shop $i", 37.5000 + (i * 0.001), 127.0000)
        }

        whenever(beautishopPort.findAllFilteredWithoutPaging(any())).thenReturn(shops)

        val command = ListBeautishopsCommand(
            page = 0,
            size = 3,
            latitude = 37.5000,
            longitude = 127.0000,
            radiusKm = 50.0
        )
        val result = useCase.execute(command)

        assertEquals(3, result.items.size)
        assertEquals(true, result.hasNext)
        assertEquals(10, result.totalElements)
    }

    private fun createBeautishop(name: String): Beautishop {
        return createBeautishopWithGps(name, 37.5665, 126.9780)
    }

    private fun createBeautishopWithGps(name: String, latitude: Double, longitude: Double): Beautishop {
        return Beautishop.create(
            name = ShopName.of(name),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구 테헤란로 123"),
            gps = ShopGPS.of(latitude, longitude),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = ShopDescription.of("아름다운 네일샵입니다"),
            images = ShopImages.of(listOf("https://example.com/image.jpg"))
        )
    }
}
