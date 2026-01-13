package com.mad.jellomarkserver.treatment.core.domain.model

import com.mad.jellomarkserver.treatment.core.domain.exception.InvalidTreatmentDescriptionException

@JvmInline
value class TreatmentDescription private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 500

        fun of(input: String): TreatmentDescription {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(trimmed.length <= MAX_LENGTH)
                return TreatmentDescription(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidTreatmentDescriptionException(input)
            }
        }

        fun ofNullable(input: String?): TreatmentDescription? {
            if (input.isNullOrBlank()) {
                return null
            }
            return of(input)
        }
    }
}
