package com.mad.jellomarkserver.treatment.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.treatment.core.domain.model.Treatment
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import com.mad.jellomarkserver.treatment.port.driving.ListTreatmentsCommand
import com.mad.jellomarkserver.treatment.port.driving.ListTreatmentsUseCase
import org.springframework.stereotype.Service
import java.util.*

@Service
class ListTreatmentsUseCaseImpl(
    private val treatmentPort: TreatmentPort
) : ListTreatmentsUseCase {

    override fun execute(command: ListTreatmentsCommand): List<Treatment> {
        val shopId = ShopId.from(UUID.fromString(command.shopId))

        return treatmentPort.findByShopId(shopId)
    }
}
