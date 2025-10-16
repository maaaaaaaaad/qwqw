package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidPhoneNumberException

class PhoneNumber private constructor(val value: String) {
    companion object {
        fun of(input: String): PhoneNumber {
            val trimmed = input.trim()
            try {
                require(input.isNotBlank())
                require(trimmed.length == 8)
                require(trimmed.all { it.isDigit() })
                return PhoneNumber(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidPhoneNumberException(input)
            }
        }
    }
}