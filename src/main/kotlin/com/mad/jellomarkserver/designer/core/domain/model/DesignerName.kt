package com.mad.jellomarkserver.designer.core.domain.model

import com.mad.jellomarkserver.designer.core.domain.exception.InvalidDesignerNameException

@JvmInline
value class DesignerName private constructor(val value: String) {
    companion object {
        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 30

        fun of(input: String): DesignerName {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(trimmed.length >= MIN_LENGTH)
                require(trimmed.length <= MAX_LENGTH)
                return DesignerName(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidDesignerNameException(input)
            }
        }
    }
}
