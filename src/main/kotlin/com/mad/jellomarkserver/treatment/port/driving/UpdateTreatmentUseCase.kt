package com.mad.jellomarkserver.treatment.port.driving

import com.mad.jellomarkserver.treatment.core.domain.model.Treatment

fun interface UpdateTreatmentUseCase {
    fun update(command: UpdateTreatmentCommand): Treatment
}
