package com.mad.jellomarkserver.treatment.core.application

import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.exception.UnauthorizedTreatmentAccessException
import com.mad.jellomarkserver.treatment.core.domain.model.*
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import com.mad.jellomarkserver.treatment.port.driving.UpdateTreatmentCommand
import com.mad.jellomarkserver.treatment.port.driving.UpdateTreatmentUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UpdateTreatmentUseCaseImpl(
    private val treatmentPort: TreatmentPort,
    private val beautishopPort: BeautishopPort
) : UpdateTreatmentUseCase {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun update(command: UpdateTreatmentCommand): Treatment {
        val treatmentId = TreatmentId.from(UUID.fromString(command.treatmentId))
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))

        val treatment = treatmentPort.findById(treatmentId)
            ?: throw TreatmentNotFoundException(command.treatmentId)

        val ownerShops = beautishopPort.findByOwnerId(ownerId)
        val ownsShop = ownerShops.any { it.id == treatment.shopId }

        if (!ownsShop) {
            throw UnauthorizedTreatmentAccessException(command.treatmentId)
        }

        val updatedTreatment = treatment.update(
            name = TreatmentName.of(command.treatmentName),
            price = TreatmentPrice.of(command.price),
            duration = TreatmentDuration.of(command.duration),
            description = TreatmentDescription.ofNullable(command.description)
        )

        return treatmentPort.save(updatedTreatment)
    }
}
