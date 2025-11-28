package com.mad.jellomarkserver.auth.core.domain.model

import com.mad.jellomarkserver.auth.core.domain.exception.InvalidRawPasswordException

@JvmInline
value class RawPassword private constructor(val value: String) {
    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 72
        private val uppercasePattern = Regex(".*[A-Z].*")
        private val lowercasePattern = Regex(".*[a-z].*")
        private val digitPattern = Regex(".*[0-9].*")
        private val specialCharPattern = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")

        fun of(input: String): RawPassword {
            validate(input)
            return RawPassword(input)
        }

        private fun validate(input: String) {
            when {
                input.isBlank() -> throw InvalidRawPasswordException("Password cannot be blank")
                input.length < MIN_LENGTH -> throw InvalidRawPasswordException("Password must be at least $MIN_LENGTH characters")
                input.length > MAX_LENGTH -> throw InvalidRawPasswordException("Password must not exceed $MAX_LENGTH characters")
                !uppercasePattern.matches(input) -> throw InvalidRawPasswordException("Password must contain at least one uppercase letter")
                !lowercasePattern.matches(input) -> throw InvalidRawPasswordException("Password must contain at least one lowercase letter")
                !digitPattern.matches(input) -> throw InvalidRawPasswordException("Password must contain at least one digit")
                !specialCharPattern.matches(input) -> throw InvalidRawPasswordException("Password must contain at least one special character")
            }
        }
    }
}
