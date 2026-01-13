package com.mad.jellomarkserver.treatment.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.exception.UnauthorizedBeautishopAccessException
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.treatment.core.domain.model.*
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import com.mad.jellomarkserver.treatment.port.driving.CreateTreatmentCommand
import com.mad.jellomarkserver.treatment.port.driving.CreateTreatmentUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CreateTreatmentUseCaseImpl(
    private val treatmentPort: TreatmentPort,
    private val beautishopPort: BeautishopPort
) : CreateTreatmentUseCase {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun create(command: CreateTreatmentCommand): Treatment {
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))
        val shopId = ShopId.from(UUID.fromString(command.shopId))

        val ownerShops = beautishopPort.findByOwnerId(ownerId)
        val shop = ownerShops.find { it.id == shopId }
            ?: run {
                val existingShop = beautishopPort.findById(shopId)
                    ?: throw BeautishopNotFoundException(shopId.value.toString())
                throw UnauthorizedBeautishopAccessException(existingShop.id.value.toString())
            }

        val treatment = Treatment.create(
            shopId = shop.id,
            name = TreatmentName.of(command.treatmentName),
            price = TreatmentPrice.of(command.price),
            duration = TreatmentDuration.of(command.duration),
            description = TreatmentDescription.ofNullable(command.description)
        )

        return treatmentPort.save(treatment)
    }
}
