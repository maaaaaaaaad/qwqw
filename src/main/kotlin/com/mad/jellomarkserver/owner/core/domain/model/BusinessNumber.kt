package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerBusinessNumberException

@JvmInline
value class BusinessNumber private constructor(val value: String) {
    companion object {
        fun of(input: String): BusinessNumber {
            val digitsOnly = input.trim().replace("-", "")
            if (digitsOnly.length != 10 || !digitsOnly.all { it.isDigit() }) {
                throw InvalidOwnerBusinessNumberException(input)
            }
            return BusinessNumber(digitsOnly)
        }
    }
}