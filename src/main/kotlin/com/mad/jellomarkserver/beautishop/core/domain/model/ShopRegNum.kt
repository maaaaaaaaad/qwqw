package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopRegNumException

@JvmInline
value class ShopRegNum private constructor(val value: String) {
    companion object {
        private const val REQUIRED_DIGIT_COUNT = 10
        private val DIGIT_ONLY_REGEX = Regex("^\\d+$")

        fun of(input: String): ShopRegNum {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())

                val digitsOnly = trimmed.replace("-", "")

                require(digitsOnly.matches(DIGIT_ONLY_REGEX))
                require(digitsOnly.length == REQUIRED_DIGIT_COUNT)

                val formatted =
                    "${digitsOnly.take(3)}-${digitsOnly.substring(3, 5)}-${digitsOnly.substring(5, 10)}"

                return ShopRegNum(formatted)
            } catch (ex: IllegalArgumentException) {
                throw InvalidShopRegNumException(input)
            } catch (ex: IndexOutOfBoundsException) {
                throw InvalidShopRegNumException(input)
            }
        }
    }
}
