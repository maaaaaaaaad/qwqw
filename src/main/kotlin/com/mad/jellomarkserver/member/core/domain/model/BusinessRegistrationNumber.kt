package com.mad.jellomarkserver.member.core.domain.model

@JvmInline
value class BusinessRegistrationNumber private constructor(val value: String) {
    companion object {
        private val digitRegex = Regex("^[0-9]{10}$")
        fun of(input: String?): BusinessRegistrationNumber {
            require(!input.isNullOrBlank())
            val normalized = input.replace("-", "").trim()
            require(digitRegex.matches(normalized))
            return BusinessRegistrationNumber(normalized)
        }
    }
}
