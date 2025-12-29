package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopImageException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ShopImageTest {

    @Test
    fun `should create ShopImage with valid HTTP URL`() {
        val image = ShopImage.of("http://example.com/image.jpg")
        assertEquals("http://example.com/image.jpg", image.value)
    }

    @Test
    fun `should create ShopImage with valid HTTPS URL`() {
        val image = ShopImage.of("https://example.com/image.png")
        assertEquals("https://example.com/image.png", image.value)
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
        val image = ShopImage.of("  https://example.com/image.jpg  ")
        assertEquals("https://example.com/image.jpg", image.value)
    }

    @Test
    fun `should allow image URL with query parameters`() {
        val image = ShopImage.of("https://example.com/image.jpg?size=large&quality=high")
        assertEquals("https://example.com/image.jpg?size=large&quality=high", image.value)
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
