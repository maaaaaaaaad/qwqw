package com.mad.jellomarkserver.treatment.core.domain.exception

class InvalidTreatmentDurationException(val duration: Int) : RuntimeException(
    "Invalid treatment duration: $duration minutes"
)
