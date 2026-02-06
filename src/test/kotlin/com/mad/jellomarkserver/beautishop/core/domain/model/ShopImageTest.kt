package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopImageException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ShopImageTest {

    @Test
    fun `should create ShopImage with valid HTTP URL`() {
        val images = ShopImages.of(listOf("http://example.com/image.jpg"))
        assertEquals("http://example.com/image.jpg", images.values[0].value)
    }

    @Test
    fun `should create ShopImage with valid HTTPS URL`() {
        val images = ShopImages.of(listOf("https://example.com/image.png"))
        assertEquals("https://example.com/image.png", images.values[0].value)
    }

    @Test
    fun `should create ShopImage with null when input is null`() {
        val image = ShopImage.ofNullable(null)
        assertNull(image)
    }

    @Test
    fun `should create ShopImage with null when input is blank`() {
        val image = ShopImage.ofNullable("   ")
        assertNull(image)
    }

    @Test
    fun `should create ShopImage with null when input is empty`() {
        val image = ShopImage.ofNullable("")
        assertNull(image)
    }

    @Test
    fun `should trim whitespace from image URL`() {
        val images = ShopImages.of(listOf("  https://example.com/image.jpg  "))
        assertEquals("https://example.com/image.jpg", images.values[0].value)
    }

    @Test
    fun `should allow image URL with query parameters`() {
        val images = ShopImages.of(listOf("https://example.com/image.jpg?size=large&quality=high"))
        assertEquals("https://example.com/image.jpg?size=large&quality=high", images.values[0].value)
    }

    @Test
    fun `should throw InvalidShopImageException when URL is blank`() {
        assertFailsWith<InvalidShopImageException> {
            ShopImage.of("   ")
        }
    }

    @Test
    fun `should throw InvalidShopImageException when URL is invalid`() {
        assertFailsWith<InvalidShopImageException> {
            ShopImage.of("not-a-valid-url")
        }
    }

    @Test
    fun `should throw InvalidShopImageException when protocol is missing`() {
        assertFailsWith<InvalidShopImageException> {
            ShopImage.of("example.com/image.jpg")
        }
    }
}
