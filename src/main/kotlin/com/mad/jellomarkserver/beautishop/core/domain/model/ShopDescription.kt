package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopDescriptionException

@JvmInline
value class ShopDescription private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 500

        fun of(input: String): ShopDescription {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(trimmed.length <= MAX_LENGTH)
                return ShopDescription(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidShopDescriptionException(input)
            }
        }

        fun ofNullable(input: String?): ShopDescription? {
            if (input.isNullOrBlank()) {
                return null
            }
            return of(input)
        }
    }
}
