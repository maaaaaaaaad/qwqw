package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidBusinessNumberException

@JvmInline
value class BusinessNumber private constructor(val value: String) {
    companion object {
        fun of(input: String): BusinessNumber {
            require(input.isNotBlank())
            val trimmed = input.trim()
            try {
                require(trimmed.length == 9)
                return BusinessNumber(trimmed)

            } catch (ex: Exception) {
                if (ex is IllegalArgumentException) InvalidBusinessNumberException(input)
                throw ex
            }
        }
    }
}