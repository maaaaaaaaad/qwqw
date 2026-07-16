package com.mad.jellomarkserver.designer.core.domain.model

import com.mad.jellomarkserver.designer.core.domain.exception.InvalidDesignerPhotosException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DesignerPhotosTest {

    @Test
    fun `should create with valid urls`() {
        val urls = listOf("https://example.com/1.jpg", "https://example.com/2.jpg")
        val photos = DesignerPhotos.of(urls)
        assertEquals(2, photos.size)
        assertEquals(urls, photos.toStringList())
    }

    @Test
    fun `should filter blank urls`() {
        val urls = listOf("https://example.com/1.jpg", "  ", "https://example.com/2.jpg")
        val photos = DesignerPhotos.of(urls)
        assertEquals(2, photos.size)
    }

    @Test
    fun `should throw when count exceeds max`() {
        val urls = (1..6).map { "https://example.com/$it.jpg" }
        assertFailsWith<InvalidDesignerPhotosException> {
            DesignerPhotos.of(urls)
        }
    }

    @Test
    fun `should accept maximum photos`() {
        val urls = (1..5).map { "https://example.com/$it.jpg" }
        val photos = DesignerPhotos.of(urls)
        assertEquals(5, photos.size)
    }

    @Test
    fun `empty should return empty photos`() {
        val photos = DesignerPhotos.empty()
        assertTrue(photos.isEmpty())
    }

    @Test
    fun `ofNullable should return empty when null`() {
        val photos = DesignerPhotos.ofNullable(null)
        assertTrue(photos.isEmpty())
    }

    @Test
    fun `ofNullable should return empty when empty list`() {
        val photos = DesignerPhotos.ofNullable(emptyList())
        assertTrue(photos.isEmpty())
    }

    @Test
    fun `equals should compare by values`() {
        val urls = listOf("https://example.com/1.jpg")
        val a = DesignerPhotos.of(urls)
        val b = DesignerPhotos.of(urls)
        assertEquals(a, b)
    }
}
