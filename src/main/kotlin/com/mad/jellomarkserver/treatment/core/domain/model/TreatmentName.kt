package com.mad.jellomarkserver.treatment.core.domain.model

import com.mad.jellomarkserver.treatment.core.domain.exception.InvalidTreatmentNameException

@JvmInline
value class TreatmentName private constructor(val value: String) {
    companion object {
        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 50

        fun of(input: String): TreatmentName {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(trimmed.length >= MIN_LENGTH)
                require(trimmed.length <= MAX_LENGTH)
                return TreatmentName(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidTreatmentNameException(input)
            }
        }
    }
}
