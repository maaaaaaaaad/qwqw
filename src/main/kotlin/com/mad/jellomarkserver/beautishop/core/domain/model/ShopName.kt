package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopNameException

@JvmInline
value class ShopName private constructor(val value: String) {
    companion object {
        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 50

        fun of(input: String): ShopName {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(trimmed.length >= MIN_LENGTH)
                require(trimmed.length <= MAX_LENGTH)
                return ShopName(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidShopNameException(input)
            }
        }
    }
}
