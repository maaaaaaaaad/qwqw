package com.mad.jellomarkserver.member.core.domain.model

import com.mad.jellomarkserver.member.core.domain.exception.InvalidEmailException

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        private val pattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        fun of(input: String): Email {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(pattern.matches(trimmed))
                return Email(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidEmailException(trimmed)
            }
        }
    }
}
