package com.mad.jellomarkserver.designer.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class DesignerTest {

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))
    private val updateClock = Clock.fixed(Instant.parse("2025-02-01T00:00:00Z"), ZoneId.of("UTC"))

    private fun createDesigner(): Designer {
        return Designer.create(
            shopId = ShopId.new(),
            name = DesignerName.of("김디자이너"),
            nickname = DesignerNickname.of("네일요정"),
            intro = DesignerIntro.of("10년 경력"),
            photoUrls = DesignerPhotos.of(listOf("https://example.com/1.jpg")),
            clock = fixedClock
        )
    }

    @Test
    fun `should create designer with all fields`() {
        val designer = createDesigner()

        assertNotNull(designer.id)
        assertEquals("김디자이너", designer.name.value)
        assertEquals("네일요정", designer.nickname?.value)
        assertEquals("10년 경력", designer.intro?.value)
        assertEquals(1, designer.photoUrls.size)
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), designer.createdAt)
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), designer.updatedAt)
    }

    @Test
    fun `update should only change provided fields`() {
        val designer = createDesigner()
        val originalCreatedAt = designer.createdAt

        val updated = designer.update(
            name = DesignerName.of("박디자이너"),
            clock = updateClock
        )

        assertEquals("박디자이너", updated.name.value)
        assertEquals("네일요정", updated.nickname?.value)
        assertEquals("10년 경력", updated.intro?.value)
        assertEquals(originalCreatedAt, updated.createdAt)
        assertEquals(Instant.parse("2025-02-01T00:00:00Z"), updated.updatedAt)
    }

    @Test
    fun `update should preserve id and shopId`() {
        val designer = createDesigner()

        val updated = designer.update(
            name = DesignerName.of("박디자이너"),
            clock = updateClock
        )

        assertEquals(designer.id, updated.id)
        assertEquals(designer.shopId, updated.shopId)
    }

    @Test
    fun `update with photoUrls should replace photos`() {
        val designer = createDesigner()
        val newPhotos = DesignerPhotos.of(listOf("https://example.com/new.jpg", "https://example.com/new2.jpg"))

        val updated = designer.update(
            photoUrls = newPhotos,
            clock = updateClock
        )

        assertEquals(2, updated.photoUrls.size)
    }

    @Test
    fun `update with no params should still bump updatedAt`() {
        val designer = createDesigner()

        val updated = designer.update(clock = updateClock)

        assertEquals(Instant.parse("2025-02-01T00:00:00Z"), updated.updatedAt)
        assertEquals(designer.name, updated.name)
    }

    @Test
    fun `update clearNickname should nullify nickname`() {
        val designer = createDesigner()

        val updated = designer.update(clearNickname = true, clock = updateClock)

        assertNull(updated.nickname)
    }

    @Test
    fun `update clearIntro should nullify intro`() {
        val designer = createDesigner()

        val updated = designer.update(clearIntro = true, clock = updateClock)

        assertNull(updated.intro)
    }

    @Test
    fun `reconstruct should restore designer from persisted data`() {
        val id = DesignerId.new()
        val shopId = ShopId.new()
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-02T00:00:00Z")

        val designer = Designer.reconstruct(
            id = id,
            shopId = shopId,
            name = DesignerName.of("김디자이너"),
            nickname = null,
            intro = null,
            photoUrls = DesignerPhotos.empty(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, designer.id)
        assertEquals(shopId, designer.shopId)
        assertNull(designer.nickname)
        assertNull(designer.intro)
        assertTrue(designer.photoUrls.isEmpty())
    }

    @Test
    fun `equals compares by id`() {
        val designer = createDesigner()

        val same = Designer.reconstruct(
            id = designer.id,
            shopId = ShopId.new(),
            name = DesignerName.of("다른이름"),
            nickname = null,
            intro = null,
            photoUrls = DesignerPhotos.empty(),
            createdAt = designer.createdAt,
            updatedAt = designer.updatedAt
        )

        assertEquals(designer, same)
        assertEquals(designer.hashCode(), same.hashCode())
    }
}
