package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopAddressException

@JvmInline
value class ShopAddress private constructor(val value: String) {
    companion object {
        private const val MIN_LENGTH = 5
        private const val MAX_LENGTH = 200

        fun of(input: String): ShopAddress {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(trimmed.length >= MIN_LENGTH)
                require(trimmed.length <= MAX_LENGTH)
                return ShopAddress(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidShopAddressException(input)
            }
        }
    }
}
