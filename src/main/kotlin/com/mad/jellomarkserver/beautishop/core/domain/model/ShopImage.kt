package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopImageException

@JvmInline
value class ShopImage private constructor(val value: String) {
    companion object {
        private val URL_PATTERN = Regex("^https?://.+")

        fun of(input: String): ShopImage {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(URL_PATTERN.matches(trimmed)) { "URL must start with http:// or https://" }
                return ShopImage(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidShopImageException(input)
            }
        }

        fun ofNullable(input: String?): ShopImage? {
            if (input.isNullOrBlank()) {
                return null
            }
            return of(input)
        }
    }
}
