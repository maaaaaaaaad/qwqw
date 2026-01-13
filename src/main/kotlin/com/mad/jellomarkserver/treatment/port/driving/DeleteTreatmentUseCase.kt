package com.mad.jellomarkserver.treatment.port.driving

fun interface DeleteTreatmentUseCase {
    fun delete(command: DeleteTreatmentCommand)
}
