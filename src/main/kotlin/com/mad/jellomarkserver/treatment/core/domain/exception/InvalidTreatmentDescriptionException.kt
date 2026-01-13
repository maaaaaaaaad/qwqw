package com.mad.jellomarkserver.treatment.core.domain.exception

class InvalidTreatmentDescriptionException(val description: String) : RuntimeException(
    "Invalid treatment description: $description"
)
