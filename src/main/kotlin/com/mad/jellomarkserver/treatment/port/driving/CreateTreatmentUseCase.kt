package com.mad.jellomarkserver.treatment.port.driving

import com.mad.jellomarkserver.treatment.core.domain.model.Treatment

fun interface CreateTreatmentUseCase {
    fun create(command: CreateTreatmentCommand): Treatment
}
