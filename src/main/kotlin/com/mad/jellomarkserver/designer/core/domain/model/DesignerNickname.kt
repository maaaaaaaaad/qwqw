package com.mad.jellomarkserver.designer.core.domain.model

import com.mad.jellomarkserver.designer.core.domain.exception.InvalidDesignerNicknameException

@JvmInline
value class DesignerNickname private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 30

        fun of(input: String): DesignerNickname {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(trimmed.length <= MAX_LENGTH)
                return DesignerNickname(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidDesignerNicknameException(input)
            }
        }

        fun ofNullable(input: String?): DesignerNickname? {
            if (input.isNullOrBlank()) {
                return null
            }
            return of(input)
        }
    }
}
