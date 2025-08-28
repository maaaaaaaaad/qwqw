package com.mad.jellomarkserver.domain.model.member

@JvmInline
value class Nickname private constructor(val value: String) {
    companion object {
        private val pattern = Regex("^[^\\s]{2,8}$")
        fun of(input: String?): Nickname {
            require(!input.isNullOrBlank())
            val trimmed = input.trim()
            require(pattern.matches(trimmed))
            return Nickname(trimmed)
        }
    }
}
