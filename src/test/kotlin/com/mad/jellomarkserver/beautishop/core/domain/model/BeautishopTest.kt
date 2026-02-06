package com.mad.jellomarkserver.beautishop.core.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class BeautishopTest {

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))

    @Test
    fun `should create Beautishop with all required fields`() {
        val name = ShopName.of("Beautiful Salon")
        val regNum = ShopRegNum.of("123-45-67890")
        val phoneNumber = ShopPhoneNumber.of("010-1234-5678")
        val address = ShopAddress.of("서울특별시 강남구 테헤란로 123")
        val gps = ShopGPS.of(37.5665, 126.9780)
        val operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00"))

        val beautishop = Beautishop.create(
            name = name,
            regNum = regNum,
            phoneNumber = phoneNumber,
            address = address,
            gps = gps,
            operatingTime = operatingTime,
            description = null,
            images = ShopImages.ofNullable(null),
            clock = fixedClock
        )

        assertNotNull(beautishop.id)
        assertEquals(name, beautishop.name)
        assertEquals(regNum, beautishop.regNum)
        assertEquals(phoneNumber, beautishop.phoneNumber)
        assertEquals(address, beautishop.address)
        assertEquals(gps, beautishop.gps)
        assertEquals(operatingTime, beautishop.operatingTime)
        assertNull(beautishop.description)
        assertEquals(0, beautishop.images.size)
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), beautishop.createdAt)
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), beautishop.updatedAt)
    }

    @Test
    fun `should create Beautishop with optional fields`() {
        val name = ShopName.of("Beautiful Salon")
        val regNum = ShopRegNum.of("123-45-67890")
        val phoneNumber = ShopPhoneNumber.of("010-1234-5678")
        val address = ShopAddress.of("서울특별시 강남구 테헤란로 123")
        val gps = ShopGPS.of(37.5665, 126.9780)
        val operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00"))
        val description = ShopDescription.of("아름다운 네일샵입니다")
        val images = ShopImages.of(listOf("https://example.com/image.jpg"))

        val beautishop = Beautishop.create(
            name = name,
            regNum = regNum,
            phoneNumber = phoneNumber,
            address = address,
            gps = gps,
            operatingTime = operatingTime,
            description = description,
            images = images,
            clock = fixedClock
        )

        assertEquals(description, beautishop.description)
        assertEquals(images, beautishop.images)
    }

    @Test
    fun `should reconstruct Beautishop from persistence`() {
        val id = ShopId.new()
        val name = ShopName.of("Beautiful Salon")
        val regNum = ShopRegNum.of("123-45-67890")
        val phoneNumber = ShopPhoneNumber.of("010-1234-5678")
        val address = ShopAddress.of("서울특별시 강남구 테헤란로 123")
        val gps = ShopGPS.of(37.5665, 126.9780)
        val operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-02T00:00:00Z")

        val beautishop = Beautishop.reconstruct(
            id = id,
            name = name,
            regNum = regNum,
            phoneNumber = phoneNumber,
            address = address,
            gps = gps,
            operatingTime = operatingTime,
            description = null,
            images = ShopImages.ofNullable(null),
            averageRating = AverageRating.zero(),
            reviewCount = ReviewCount.zero(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, beautishop.id)
        assertEquals(name, beautishop.name)
        assertEquals(createdAt, beautishop.createdAt)
        assertEquals(updatedAt, beautishop.updatedAt)
    }

    @Test
    fun `should update modifiable fields`() {
        val beautishop = Beautishop.create(
            name = ShopName.of("Beautiful Salon"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구 테헤란로 123"),
            gps = ShopGPS.of(37.5665, 126.9780),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = null,
            images = ShopImages.ofNullable(null),
            clock = fixedClock
        )

        val newOperatingTime = OperatingTime.of(mapOf("monday" to "10:00-19:00"))
        val newDescription = ShopDescription.of("새로운 소개문구")
        val newImages = ShopImages.of(listOf("https://example.com/new-image.jpg"))
        val updateClock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneId.of("UTC"))

        val updated = beautishop.update(
            operatingTime = newOperatingTime,
            description = newDescription,
            images = newImages,
            clock = updateClock
        )

        assertEquals(newOperatingTime, updated.operatingTime)
        assertEquals(newDescription, updated.description)
        assertEquals(newImages, updated.images)
        assertEquals(Instant.parse("2025-01-02T00:00:00Z"), updated.updatedAt)
        assertEquals(beautishop.createdAt, updated.createdAt)
    }

    @Test
    fun `should maintain identity equality based on id`() {
        val id = ShopId.new()
        val name = ShopName.of("Beautiful Salon")
        val regNum = ShopRegNum.of("123-45-67890")
        val phoneNumber = ShopPhoneNumber.of("010-1234-5678")
        val address = ShopAddress.of("서울특별시 강남구 테헤란로 123")
        val gps = ShopGPS.of(37.5665, 126.9780)
        val operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00"))
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val beautishop1 = Beautishop.reconstruct(
            id = id,
            name = name,
            regNum = regNum,
            phoneNumber = phoneNumber,
            address = address,
            gps = gps,
            operatingTime = operatingTime,
            description = null,
            images = ShopImages.ofNullable(null),
            averageRating = AverageRating.zero(),
            reviewCount = ReviewCount.zero(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
        val beautishop2 = Beautishop.reconstruct(
            id = id,
            name = name,
            regNum = regNum,
            phoneNumber = phoneNumber,
            address = address,
            gps = gps,
            operatingTime = operatingTime,
            description = null,
            images = ShopImages.ofNullable(null),
            averageRating = AverageRating.zero(),
            reviewCount = ReviewCount.zero(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(beautishop1, beautishop2)
        assertEquals(beautishop1.hashCode(), beautishop2.hashCode())
    }
}
