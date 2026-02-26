package com.mad.jellomarkserver.owner.core.domain.model

import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerPhoneNumberException

@JvmInline
value class OwnerPhoneNumber private constructor(val value: String) {
    companion object {
        private val mobilePattern = Regex("^(010|011|016|017|018|019)(\\d{3,4})(\\d{4})$")
        private val seoulPattern = Regex("^(02)(\\d{3,4})(\\d{4})$")
        private val regionalPattern = Regex("^(0(?:3[1-3]|4[1-4]|5[1-5]|6[1-4]))(\\d{3,4})(\\d{4})$")

        fun of(input: String): OwnerPhoneNumber {
            val digitsOnly = input.trim().replace("-", "")

            val match = mobilePattern.matchEntire(digitsOnly)
                ?: seoulPattern.matchEntire(digitsOnly)
                ?: regionalPattern.matchEntire(digitsOnly)
                ?: throw InvalidOwnerPhoneNumberException(input)

            val formatted = "${match.groupValues[1]}-${match.groupValues[2]}-${match.groupValues[3]}"
            return OwnerPhoneNumber(formatted)
        }
    }
}