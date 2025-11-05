package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerBusinessNumberException

@JvmInline
value class BusinessNumber private constructor(val value: String) {
    companion object {
        fun of(input: String): BusinessNumber {
            val trimmed = input.trim()
            try {
                require(input.isNotBlank())
                require(trimmed.length == 9)
                return BusinessNumber(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidOwnerBusinessNumberException(input)
            }
        }
    }
}