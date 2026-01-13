package com.mad.jellomarkserver.treatment.core.domain.model

import com.mad.jellomarkserver.treatment.core.domain.exception.InvalidTreatmentPriceException

@JvmInline
value class TreatmentPrice private constructor(val value: Int) {
    companion object {
        fun of(price: Int): TreatmentPrice {
            try {
                require(price >= 0)
                return TreatmentPrice(price)
            } catch (ex: IllegalArgumentException) {
                throw InvalidTreatmentPriceException(price)
            }
        }
    }
}
