package com.mad.jellomarkserver.beautishop.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopPhoneNumberException

@JvmInline
value class ShopPhoneNumber private constructor(val value: String) {
    companion object {
        private val pattern =
            Regex("^(010|011|016|017|018|019)-\\d{3,4}-\\d{4}$|^02-\\d{3,4}-\\d{4}$|^0(3[1-3]|4[1-4]|5[1-5]|6[1-4])-\\d{3,4}-\\d{4}$")

        fun of(input: String): ShopPhoneNumber {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(pattern.matches(trimmed))
                return ShopPhoneNumber(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidShopPhoneNumberException(trimmed)
            }
        }
    }
}
