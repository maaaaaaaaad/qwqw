package com.mad.jellomarkserver.designer.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.designer.adapter.driven.persistence.entity.DesignerJpaEntity
import com.mad.jellomarkserver.designer.core.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class DesignerMapperImplTest {

    private val mapper = DesignerMapperImpl()

    @Test
    fun `should map entity to domain with all fields`() {
        val id = UUID.randomUUID()
        val shopId = UUID.randomUUID()
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-02T00:00:00Z")

        val entity = DesignerJpaEntity(
            id = id,
            shopId = shopId,
            name = "김디자이너",
            nickname = "네일요정",
            intro = "10년 경력",
            photoUrls = "https://example.com/1.jpg|https://example.com/2.jpg",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = mapper.toDomain(entity)

        assertEquals(DesignerId.from(id), result.id)
        assertEquals(ShopId.from(shopId), result.shopId)
        assertEquals("김디자이너", result.name.value)
        assertEquals("네일요정", result.nickname?.value)
        assertEquals("10년 경력", result.intro?.value)
        assertEquals(2, result.photoUrls.size)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should map entity to domain with null nickname intro photos`() {
        val entity = DesignerJpaEntity(
            id = UUID.randomUUID(),
            shopId = UUID.randomUUID(),
            name = "김디자이너",
            nickname = null,
            intro = null,
            photoUrls = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val result = mapper.toDomain(entity)

        assertNull(result.nickname)
        assertNull(result.intro)
        assertTrue(result.photoUrls.isEmpty())
    }

    @Test
    fun `should map domain to entity`() {
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-02T00:00:00Z")

        val domain = Designer.reconstruct(
            id = DesignerId.new(),
            shopId = ShopId.new(),
            name = DesignerName.of("김디자이너"),
            nickname = DesignerNickname.of("네일요정"),
            intro = DesignerIntro.of("10년 경력"),
            photoUrls = DesignerPhotos.of(listOf("https://example.com/1.jpg", "https://example.com/2.jpg")),
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entity = mapper.toEntity(domain)

        assertEquals(domain.id.value, entity.id)
        assertEquals(domain.shopId.value, entity.shopId)
        assertEquals("김디자이너", entity.name)
        assertEquals("네일요정", entity.nickname)
        assertEquals("10년 경력", entity.intro)
        assertEquals("https://example.com/1.jpg|https://example.com/2.jpg", entity.photoUrls)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should serialize empty photos to null`() {
        val domain = Designer.reconstruct(
            id = DesignerId.new(),
            shopId = ShopId.new(),
            name = DesignerName.of("김디자이너"),
            nickname = null,
            intro = null,
            photoUrls = DesignerPhotos.empty(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val entity = mapper.toEntity(domain)

        assertNull(entity.photoUrls)
    }

    @Test
    fun `roundtrip should preserve all fields`() {
        val original = Designer.reconstruct(
            id = DesignerId.new(),
            shopId = ShopId.new(),
            name = DesignerName.of("김디자이너"),
            nickname = DesignerNickname.of("네일요정"),
            intro = DesignerIntro.of("10년 경력"),
            photoUrls = DesignerPhotos.of(listOf("https://example.com/1.jpg", "https://example.com/2.jpg")),
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-02T00:00:00Z")
        )

        val roundTripped = mapper.toDomain(mapper.toEntity(original))

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.shopId, roundTripped.shopId)
        assertEquals(original.name, roundTripped.name)
        assertEquals(original.nickname, roundTripped.nickname)
        assertEquals(original.intro, roundTripped.intro)
        assertEquals(original.photoUrls.toStringList(), roundTripped.photoUrls.toStringList())
        assertEquals(original.createdAt, roundTripped.createdAt)
        assertEquals(original.updatedAt, roundTripped.updatedAt)
    }
}
