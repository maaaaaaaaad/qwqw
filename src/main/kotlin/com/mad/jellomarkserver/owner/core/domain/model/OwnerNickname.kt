package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerNicknameException

@JvmInline
value class OwnerNickname private constructor(val value: String) {
    companion object {
        private val pattern = Regex("^\\S{2,8}$")
        fun of(input: String): OwnerNickname {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(pattern.matches(trimmed))
                return OwnerNickname(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidOwnerNicknameException(trimmed)
            }
        }
    }
}