package com.mad.jellomarkserver.member.core.domain.model

import com.mad.jellomarkserver.member.core.domain.exception.InvalidNicknameException

@JvmInline
value class Nickname private constructor(val value: String) {
    companion object {
        private val pattern = Regex("^\\S{2,8}$")
        fun of(input: String): Nickname {
            require(input.isNotBlank())
            val trimmed = input.trim()
            try {
                require(pattern.matches(trimmed))
                return Nickname(trimmed)

            } catch (ex: Exception) {
                if (ex is IllegalArgumentException) InvalidNicknameException(input)
                throw ex
            }
        }
    }
}
