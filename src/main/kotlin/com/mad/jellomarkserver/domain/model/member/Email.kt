package com.mad.jellomarkserver.domain.model.member

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        private val pattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        fun of(input: String?): Email {
            require(!input.isNullOrBlank())
            val trimmed = input.trim()
            require(pattern.matches(trimmed))
            return Email(trimmed)
        }
    }
}
