package com.mad.jellomarkserver.member.core.domain.model

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
                throw ex //Nickname Invalid exception 필요함
            }
        }
    }
}
