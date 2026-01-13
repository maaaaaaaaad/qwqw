package com.mad.jellomarkserver.treatment.core.application

import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.model.Treatment
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import com.mad.jellomarkserver.treatment.port.driving.GetTreatmentCommand
import com.mad.jellomarkserver.treatment.port.driving.GetTreatmentUseCase
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetTreatmentUseCaseImpl(
    private val treatmentPort: TreatmentPort
) : GetTreatmentUseCase {

    override fun execute(command: GetTreatmentCommand): Treatment {
        val treatmentId = TreatmentId.from(UUID.fromString(command.treatmentId))

        return treatmentPort.findById(treatmentId)
            ?: throw TreatmentNotFoundException(command.treatmentId)
    }
}
