package com.mad.jellomarkserver.treatment.core.domain.exception

class InvalidTreatmentPriceException(val price: Int) : RuntimeException(
    "Invalid treatment price: $price"
)
