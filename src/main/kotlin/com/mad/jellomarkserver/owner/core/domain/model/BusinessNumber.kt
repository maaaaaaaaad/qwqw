package com.mad.jellomarkserver.owner.core.domain.model

@JvmInline
value class BusinessNumber private constructor(val value: String) {
    companion object {
        fun of(input: String?): BusinessNumber {
            require(!input.isNullOrBlank())
            val trimmed = input.trim()
            require(trimmed.length == 9)
            return BusinessNumber(trimmed)
        }
    }
}