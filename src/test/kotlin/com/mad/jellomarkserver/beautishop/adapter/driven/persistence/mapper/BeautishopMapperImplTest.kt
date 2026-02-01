package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.entity.BeautishopJpaEntity
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class BeautishopMapperImplTest {

    private val mapper = BeautishopMapperImpl()

    @Test
    fun `should correctly map BeautishopJpaEntity to Beautishop`() {
        val id = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val entity = BeautishopJpaEntity(
            id = id,
            ownerId = ownerId,
            name = "Beautiful Salon",
            shopRegNum = "123-45-67890",
            phoneNumber = "010-1234-5678",
            address = "서울특별시 강남구 테헤란로 123",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = "monday:09:00-18:00,tuesday:09:00-18:00",
            description = "아름다운 네일샵입니다",
            image = "https://example.com/image.jpg",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = mapper.toDomain(entity)

        assertEquals(ShopId.from(id), result.id)
        assertEquals(ShopName.of("Beautiful Salon"), result.name)
        assertEquals(ShopRegNum.of("123-45-67890"), result.regNum)
        assertEquals(ShopPhoneNumber.of("010-1234-5678"), result.phoneNumber)
        assertEquals(ShopAddress.of("서울특별시 강남구 테헤란로 123"), result.address)
        assertEquals(37.5665, result.gps.latitude)
        assertEquals(126.9780, result.gps.longitude)
        assertEquals(mapOf("monday" to "09:00-18:00", "tuesday" to "09:00-18:00"), result.operatingTime.schedule)
        assertEquals(ShopDescription.of("아름다운 네일샵입니다"), result.description)
        assertEquals(ShopImage.of("https://example.com/image.jpg"), result.image)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map BeautishopJpaEntity with null optional fields`() {
        val id = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val entity = BeautishopJpaEntity(
            id = id,
            ownerId = ownerId,
            name = "Beautiful Salon",
            shopRegNum = "123-45-67890",
            phoneNumber = "010-1234-5678",
            address = "서울특별시 강남구 테헤란로 123",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = "monday:09:00-18:00",
            description = null,
            images = ShopImages.ofNullable(null),
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = mapper.toDomain(entity)

        assertNull(result.description)
        assertNull(result.image)
    }

    @Test
    fun `should correctly map Beautishop to BeautishopJpaEntity`() {
        val id = ShopId.new()
        val ownerId = OwnerId.new()
        val name = ShopName.of("Beautiful Salon")
        val regNum = ShopRegNum.of("123-45-67890")
        val phoneNumber = ShopPhoneNumber.of("010-1234-5678")
        val address = ShopAddress.of("서울특별시 강남구 테헤란로 123")
        val gps = ShopGPS.of(37.5665, 126.9780)
        val operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00", "tuesday" to "09:00-18:00"))
        val description = ShopDescription.of("아름다운 네일샵입니다")
        val images = ShopImages.of(listOf("https://example.com/image.jpg"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val domain = Beautishop.reconstruct(
            id = id,
            name = name,
            regNum = regNum,
            phoneNumber = phoneNumber,
            address = address,
            gps = gps,
            operatingTime = operatingTime,
            description = description,
            image = image,
            averageRating = AverageRating.of(4.5),
            reviewCount = ReviewCount.of(10),
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entity = mapper.toEntity(domain, ownerId)

        assertEquals(id.value, entity.id)
        assertEquals(ownerId.value, entity.ownerId)
        assertEquals("Beautiful Salon", entity.name)
        assertEquals("123-45-67890", entity.shopRegNum)
        assertEquals("010-1234-5678", entity.phoneNumber)
        assertEquals("서울특별시 강남구 테헤란로 123", entity.address)
        assertEquals(37.5665, entity.latitude)
        assertEquals(126.9780, entity.longitude)
        assertEquals("아름다운 네일샵입니다", entity.description)
        assertEquals("https://example.com/image.jpg", entity.image)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should correctly map Beautishop with null optional fields to BeautishopJpaEntity`() {
        val id = ShopId.new()
        val ownerId = OwnerId.new()
        val name = ShopName.of("Beautiful Salon")
        val regNum = ShopRegNum.of("123-45-67890")
        val phoneNumber = ShopPhoneNumber.of("010-1234-5678")
        val address = ShopAddress.of("서울특별시 강남구 테헤란로 123")
        val gps = ShopGPS.of(37.5665, 126.9780)
        val operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val domain = Beautishop.reconstruct(
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

        val entity = mapper.toEntity(domain, ownerId)

        assertNull(entity.description)
        assertNull(entity.image)
    }

    @Test
    fun `domain - entity - domain round-trip should keep values`() {
        val id = ShopId.new()
        val ownerId = OwnerId.new()
        val name = ShopName.of("Beautiful Salon")
        val regNum = ShopRegNum.of("123-45-67890")
        val phoneNumber = ShopPhoneNumber.of("010-1234-5678")
        val address = ShopAddress.of("서울특별시 강남구 테헤란로 123")
        val gps = ShopGPS.of(37.5665, 126.9780)
        val operatingTime = OperatingTime.of(
            mapOf(
                "monday" to "09:00-18:00",
                "tuesday" to "09:00-18:00",
                "wednesday" to "09:00-18:00"
            )
        )
        val description = ShopDescription.of("아름다운 네일샵입니다")
        val images = ShopImages.of(listOf("https://example.com/image.jpg"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val original = Beautishop.reconstruct(
            id = id,
            name = name,
            regNum = regNum,
            phoneNumber = phoneNumber,
            address = address,
            gps = gps,
            operatingTime = operatingTime,
            description = description,
            image = image,
            averageRating = AverageRating.of(4.2),
            reviewCount = ReviewCount.of(5),
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entity = mapper.toEntity(original, ownerId)
        val roundTripped = mapper.toDomain(entity)

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.name, roundTripped.name)
        assertEquals(original.regNum, roundTripped.regNum)
        assertEquals(original.phoneNumber, roundTripped.phoneNumber)
        assertEquals(original.address, roundTripped.address)
        assertEquals(original.gps, roundTripped.gps)
        assertEquals(original.operatingTime.schedule, roundTripped.operatingTime.schedule)
        assertEquals(original.description, roundTripped.description)
        assertEquals(original.image, roundTripped.image)
        assertEquals(original.createdAt, roundTripped.createdAt)
        assertEquals(original.updatedAt, roundTripped.updatedAt)
    }

    @Test
    fun `should correctly serialize and deserialize complex operating time`() {
        val operatingTime = OperatingTime.of(
            mapOf(
                "monday" to "09:00-18:00",
                "tuesday" to "10:00-20:00",
                "wednesday" to "closed",
                "thursday" to "09:00-18:00",
                "friday" to "09:00-22:00",
                "saturday" to "10:00-15:00",
                "sunday" to "closed"
            )
        )

        val beautishop = Beautishop.create(
            name = ShopName.of("Test Shop"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울시 강남구"),
            gps = ShopGPS.of(37.5, 127.0),
            operatingTime = operatingTime,
            description = null,
            images = ShopImages.ofNullable(null)
        )

        val entity = mapper.toEntity(beautishop, OwnerId.new())
        val restored = mapper.toDomain(entity)

        assertEquals(operatingTime.schedule, restored.operatingTime.schedule)
    }
}
