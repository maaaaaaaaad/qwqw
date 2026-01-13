package com.mad.jellomarkserver.treatment.port.driving

data class DeleteTreatmentCommand(
    val treatmentId: String,
    val ownerId: String
)
