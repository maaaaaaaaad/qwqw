package com.mad.jellomarkserver.treatment.port.driving

data class UpdateTreatmentCommand(
    val treatmentId: String,
    val ownerId: String,
    val treatmentName: String,
    val price: Int,
    val duration: Int,
    val description: String?
)
