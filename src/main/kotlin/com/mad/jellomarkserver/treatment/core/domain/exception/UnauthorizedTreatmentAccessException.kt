package com.mad.jellomarkserver.treatment.core.domain.exception

class UnauthorizedTreatmentAccessException(val treatmentId: String) : RuntimeException(
    "Unauthorized access to treatment: $treatmentId"
)
