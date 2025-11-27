package com.mad.jellomarkserver.auth.core.domain.model

import com.mad.jellomarkserver.auth.core.domain.exception.InvalidAuthEmailException

@JvmInline
value class AuthEmail private constructor(val value: String) {
    companion object {
        private val pattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        fun of(input: String): AuthEmail {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(pattern.matches(trimmed))
                return AuthEmail(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidAuthEmailException(trimmed)
            }
        }
    }
}
