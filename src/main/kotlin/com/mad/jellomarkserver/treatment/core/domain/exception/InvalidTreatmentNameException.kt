package com.mad.jellomarkserver.treatment.core.domain.exception

class InvalidTreatmentNameException(val name: String) : RuntimeException(
    "Invalid treatment name: $name"
)
