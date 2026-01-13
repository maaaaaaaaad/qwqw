package com.mad.jellomarkserver.treatment.core.domain.model

import com.mad.jellomarkserver.treatment.core.domain.exception.InvalidTreatmentDurationException

@JvmInline
value class TreatmentDuration private constructor(val value: Int) {
    companion object {
        private const val MIN_DURATION = 10
        private const val MAX_DURATION = 300

        fun of(minutes: Int): TreatmentDuration {
            try {
                require(minutes >= MIN_DURATION)
                require(minutes <= MAX_DURATION)
                return TreatmentDuration(minutes)
            } catch (ex: IllegalArgumentException) {
                throw InvalidTreatmentDurationException(minutes)
            }
        }
    }
}
