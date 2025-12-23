package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerEmailException

@JvmInline
value class OwnerEmail private constructor(val value: String) {
    companion object {
        private val pattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        fun of(input: String): OwnerEmail {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(pattern.matches(trimmed))
                return OwnerEmail(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidOwnerEmailException(trimmed)
            }
        }
    }
}
