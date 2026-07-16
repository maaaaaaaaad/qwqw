package com.mad.jellomarkserver.designer.core.domain.model

import com.mad.jellomarkserver.designer.core.domain.exception.InvalidDesignerIntroException

@JvmInline
value class DesignerIntro private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 500

        fun of(input: String): DesignerIntro {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(trimmed.length <= MAX_LENGTH)
                return DesignerIntro(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidDesignerIntroException(input)
            }
        }

        fun ofNullable(input: String?): DesignerIntro? {
            if (input.isNullOrBlank()) {
                return null
            }
            return of(input)
        }
    }
}
