package com.mad.jellomarkserver.treatment.core.domain.exception

class TreatmentNotFoundException(val treatmentId: String) : RuntimeException(
    "Treatment not found: $treatmentId"
)
